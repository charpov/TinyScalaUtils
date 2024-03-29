package tinyscalautils.threads

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.joined
import tinyscalautils.control.times
import tinyscalautils.text.printout
import tinyscalautils.timing.delay

import java.util.concurrent.{ CountDownLatch, ThreadFactory }

class NewThreadSuite extends AnyFunSuite:

   test("1") {
      val done   = CountDownLatch(1)
      val thread = newThread("Joe")(done.await())
      assert(thread.getName == "Joe")
      assert(!thread.isDaemon)
      assert(thread.isAlive)
      done.countDown()
      assert(thread.joined(1.0))
   }

   test("2") {
      val done   = CountDownLatch(1)
      val thread = newThread(done.await())
      assert(thread.getName.startsWith("Thread-"))
      assert(!thread.isDaemon)
      assert(thread.isAlive)
      done.countDown()
      assert(thread.joined(1.0))
   }

   test("3") {
      val done   = CountDownLatch(1)
      val thread = newThread("Joe", daemon = true)(done.await())
      assert(thread.getName == "Joe")
      assert(thread.isDaemon)
      assert(thread.isAlive)
      done.countDown()
      assert(thread.joined(1.0))
   }

   test("4") {
      val done   = CountDownLatch(1)
      val thread = newThread("Joe", start = false)(done.await())
      assert(thread.getName == "Joe")
      assert(!thread.isDaemon)
      assert(!thread.isAlive)
   }

   test("5") {
      val done   = CountDownLatch(1)
      val thread = newThread(name = "Joe", start = false, daemon = true)(done.await())
      assert(thread.getName == "Joe")
      assert(thread.isDaemon)
      assert(!thread.isAlive)
   }

   test("6") {
      val done = CountDownLatch(1)
      val thread = newThread(waitForChildren = true) {
         newThread(done.await())
      }
      assert(!thread.joined(0.5))
      done.countDown()
      assert(thread.joined(1.0))
   }

   test("7") {
      val n    = 100
      val done = CountDownLatch(1)
      val thread = newThread(waitForChildren = true) {
         n times newThread(done.await())
      }
      assert(!thread.joined(0.5))
      done.countDown()
      assert(thread.joined(1.0))
   }

   test("8") {
      def invokeAndWait(code: => Any): Unit =
         newThread(name = "main", waitForChildren = true)(code).join()
      val tf: ThreadFactory = Thread(_)
      val exec              = java.util.concurrent.Executors.newCachedThreadPool(tf)
      val str = printout {
         invokeAndWait {
            exec.run(println(delay(1.0)("X")))
            exec.shutdown()
         }
      }
      assert(str == "X\n")
   }
