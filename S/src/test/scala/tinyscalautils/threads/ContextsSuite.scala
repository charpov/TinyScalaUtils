package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.{ Assertion, Succeeded }
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.control.times
import tinyscalautils.timing.{ sleep, timeOf }
import tinyscalautils.threads.shutdownAndWait

import scala.concurrent.{ Await, Future, Promise }
import scala.concurrent.duration.{ Duration, SECONDS }
import java.util.concurrent.{ CountDownLatch, CyclicBarrier }

class ContextsSuite extends AnyFunSuite with Tolerance:

   test("withContext") {
      val exec = Executors.newUnlimitedThreadPool()
      val time = withContext(exec) {
         Future {
            timeOf(sleep(1.0))
         }
      }
      assert(!exec.isShutdown)
      assert(exec.shutdownAndWait(1.0))
      assert(time === 1.0 +- 0.01)
   }

   test("withLocalLocalThreadPool") {
      val exec = Executors.newUnlimitedThreadPool()
      val time = withLocalThreadPool(exec) {
         Future {
            timeOf(sleep(1.0))
         }
      }
      assert(exec.isShutdown)
      assert(exec.isTerminated)
      assert(time === 1.0 +- 0.1)
   }

   test("withLocalThreadPool, exception") {
      object Ex extends Exception
      val exec = Executors.newUnlimitedThreadPool()
      val ex = intercept[Exception] {
         withLocalThreadPool(exec) {
            Future {
               throw Ex
            }
         }
      }
      assert(exec.isShutdown)
      assert(exec.isTerminated)
      assert(ex eq Ex)
   }

   test("withLocalThreadPool, interrupt") {
      val exec = Executors.newUnlimitedThreadPool()
      val p    = Promise[Assertion]()
      val task: Runnable = () =>
         p.success {
            assertThrows[InterruptedException] {
               withLocalThreadPool(exec) {
                  exec.run(sleep(1.0))
                  Future.unit
               }
            }
         }
      val thread = Thread(task)
      thread.start()
      sleep(0.5)
      thread.interrupt()
      Await.ready(p.future, Duration.Inf)
      assert(exec.isShutdown)
      assert(exec.shutdownAndWait(1.0))
      assert(p.future.value.get.isSuccess)
   }

   test("withUnlimitedThreads(false)") {
      val n       = 100
      val barrier = CyclicBarrier(n)
      assert {
         timeOf {
            withUnlimitedThreads { exec =>
               n times exec.run(barrier.await())
               exec.run(sleep(1.0))
               exec.shutdown()
            }
         } === 0.0 +- 0.1
      }
   }

   test("withUnlimitedThreads(true)") {
      val n       = 100
      val barrier = CyclicBarrier(n)
      assert {
         timeOf {
            withUnlimitedThreads(waitForTermination = true) { exec =>
               n times exec.run(barrier.await())
               exec.run(sleep(1.0))
            }
         } === 1.0 +- 0.1
      }
   }
