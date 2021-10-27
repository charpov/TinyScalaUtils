package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.withLocalContext
import tinyscalautils.timing.{ delay, sleep, timeOf }

import java.util.concurrent.atomic.{ AtomicInteger, AtomicReference }
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future, TimeoutException }

class TimeoutSuite extends AnyFunSuite with Tolerance:

   private def sign(ref: AtomicReference[Thread]): Unit =
      if !ref.compareAndSet(null, Thread.currentThread) then
         throw IllegalStateException("already signed")

   private def sign[A](ref: AtomicReference[Thread], value: => A): A =
      sign(ref)
      value

   test("orTimeout") {
      assertResult("X") {
         withLocalContext(Executors.newUnlimitedThreadPool()) {
            val f = Future(delay(1.0)("X")).orTimeout(1.5)
            assert(timeOf(Await.ready(f, Duration.Inf)) === 1.0 +- 0.1)
            f
         }
      }
   }

   test("orTimeout, completed") {
      assertResult("X") {
         withLocalContext(Executors.newUnlimitedThreadPool()) {
            val f = Future.successful("X").orTimeout(0.5)
            assert(timeOf(Await.ready(f, Duration.Inf)) === 0.0 +- 0.1)
            f
         }
      }
   }

   test("orTimeout, cancel code") {
      assertResult("X") {
         withLocalContext(Executors.newUnlimitedThreadPool()) {
            val count = AtomicInteger()

            val f = Future(delay(1.0)("X")).orTimeout(1.5, cancelCode = count.incrementAndGet())
            assert(timeOf(Await.ready(f, Duration.Inf)) === 1.0 +- 0.1)
            sleep(1.0)
            assert(count.get === 0)
            f
         }
      }
   }

   test("orTimeout, completed, cancel code") {
      assertResult("X") {
         withLocalContext(Executors.newUnlimitedThreadPool()) {
            val count = AtomicInteger()

            val f = Future.successful("X").orTimeout(0.5, cancelCode = count.incrementAndGet())
            assert(timeOf(Await.ready(f, Duration.Inf)) === 0.0 +- 0.1)
            sleep(1.0)
            assert(count.get === 0)
            f
         }
      }
   }

   test("orTimeout, timeout") {
      assertResult("Y") {
         withLocalContext(Executors.newUnlimitedThreadPool()) {
            val f = Future(delay(1.5)("X"))
               .orTimeout(1.0)
               .recover {
                  case _: TimeoutException => "Y"
               }
            assert(timeOf(Await.ready(f, Duration.Inf)) === 1.0 +- 0.1)
            sleep(1.0)
            f
         }
      }
   }

   test("orTimeout, no time") {
      assertResult("Y") {
         withLocalContext(Executors.newUnlimitedThreadPool()) {
            val f = Future(delay(0.5)("X"))
               .orTimeout(-1.0)
               .recover {
                  case _: TimeoutException => "Y"
               }
            assert(timeOf(Await.ready(f, Duration.Inf)) === 0.0 +- 0.1)
            sleep(1.0)
            f
         }
      }
   }

   test("orTimeout, timeout, cancel code") {
      val tf     = KeepThreadsFactory()
      val thread = AtomicReference[Thread]()

      assertResult("Y") {
         withLocalContext(Executors.withFactory(tf).newUnlimitedThreadPool()) {
            val f = Future(delay(1.5)("X"))
               .orTimeout(1.0, cancelCode = sign(thread))
               .recover {
                  case _: TimeoutException => "Y"
               }
            assert(timeOf(Await.ready(f, Duration.Inf)) === 1.0 +- 0.1)
            sleep(1.0)
            assert(tf.allThreads.toSet.contains(thread.get))
            f
         }
      }
   }

   test("orTimeout, no time, cancel code") {
      val tf     = KeepThreadsFactory()
      val thread = AtomicReference[Thread]()

      assertResult("Y") {
         withLocalContext(Executors.withFactory(tf).newUnlimitedThreadPool()) {
            val f = Future(delay(1.0)("X"))
               .orTimeout(-1.0, cancelCode = sign(thread))
               .recover {
                  case _: TimeoutException => "Y"
               }
            assert(timeOf(Await.ready(f, Duration.Inf)) === 0.0 +- 0.1)
            sleep(0.5)
            assert(tf.allThreads.toSet.contains(thread.get))
            f
         }
      }
   }

   for (strict <- Seq(false, true)) do
      test(s"completeOnTimeout, strict=$strict") {
         val thread = AtomicReference[Thread]()

         assertResult("X") {
            withLocalContext(Executors.newUnlimitedThreadPool()) {
               val f = Future(delay(1.0)("X")).completeOnTimeout(1.5, strict)(sign(thread, "Y"))
               assert(timeOf(Await.ready(f, Duration.Inf)) === 1.0 +- 0.1)
               assert(thread.get eq null)
               f
            }
         }
      }

      test(s"completeOnTimeout, completed, strict=$strict") {
         val thread = AtomicReference[Thread]()

         assertResult("X") {
            withLocalContext(Executors.newUnlimitedThreadPool()) {
               val f = Future.successful("X").completeOnTimeout(0.5, strict)(sign(thread, "Y"))
               assert(timeOf(Await.ready(f, Duration.Inf)) === 0.0 +- 0.1)
               sleep(1.0)
               assert(thread.get eq null)
               f
            }
         }
      }

      test(s"completeOnTimeout, timeout, strict=$strict") {
         val tf     = KeepThreadsFactory()
         val thread = AtomicReference[Thread]()

         assertResult("Y") {
            withLocalContext(Executors.withFactory(tf).newUnlimitedThreadPool()) {
               val f = Future(delay(2.0)("X")).completeOnTimeout(1.0, strict)(sign(thread, "Y"))
               assert(timeOf(Await.ready(f, Duration.Inf)) === 1.0 +- 0.1)
               sleep(0.5)
               assert(tf.allThreads.toSet.contains(thread.get))
               sleep(1.0)
               f
            }
         }
      }

      test(s"completeOnTimeout, no time, strict=$strict") {
         val tf     = KeepThreadsFactory()
         val thread = AtomicReference[Thread]()

         assertResult("Y") {
            withLocalContext(Executors.withFactory(tf).newUnlimitedThreadPool()) {
               val f = Future(delay(1.0)("X")).completeOnTimeout(-1.0, strict)(sign(thread, "Y"))
               assert(timeOf(Await.ready(f, Duration.Inf)) === 0.0 +- 0.1)
               sleep(0.5)
               assert(tf.allThreads.toSet.contains(thread.get))
               sleep(1.0)
               f
            }
         }
      }

   test("completeOnTimeout, loose") {
      assertResult(42) {
         val count = AtomicInteger()

         withLocalContext(Executors.newUnlimitedThreadPool()) {
            val f =
               Future(delay(1.0)(42)).completeOnTimeout(0.5)(delay(1.0)(count.incrementAndGet()))
            assert(timeOf(Await.ready(f, Duration.Inf)) === 1.0 +- 0.1)
            sleep(1.0)
            assert(count.get === 1)
            f
         }
      }
   }

   test("completeOnTimeout, strict") {
      assertResult(1) {
         val count = AtomicInteger()

         withLocalContext(Executors.newUnlimitedThreadPool()) {
            val f = Future(delay(1.0)(42))
               .completeOnTimeout(0.5, strict = true)(delay(1.5)(count.incrementAndGet()))
            assert(timeOf(Await.ready(f, Duration.Inf)) === 2.0 +- 0.1)
            f
         }
      }
   }
