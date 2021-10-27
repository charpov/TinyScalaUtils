package tinyscalautils.threads

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.joined

import java.util.concurrent.CountDownLatch

class ThreadSuite extends AnyFunSuite:

   test("1") {
      val done   = CountDownLatch(1)
      val thread = newThread("Joe")(done.await())
      assert(thread.getName == "Joe")
      assert(!thread.isDaemon)
      assert(thread.isAlive)
      done.countDown()
   }

   test("2") {
      val done   = CountDownLatch(1)
      val thread = newThread("")(done.await())
      assert(thread.getName.startsWith("Thread-"))
      assert(!thread.isDaemon)
      assert(thread.isAlive)
      done.countDown()
   }

   test("3") {
      val done   = CountDownLatch(1)
      val thread = newThread("Joe", daemon = true)(done.await())
      assert(thread.getName == "Joe")
      assert(thread.isDaemon)
      assert(thread.isAlive)
      done.countDown()
   }

   test("4") {
      val done   = CountDownLatch(1)
      val thread = newThread("Joe", start = false)(done.await())
      assert(thread.getName == "Joe")
      assert(!thread.isDaemon)
      assert(!thread.isAlive)
   }

   test("5") {
      val done = CountDownLatch(1)
      // val thread = newThread(name = "Joe", start = false, daemon = true)(done.await())
      val thread =
         newStoppableThread(delay = 2.0, name = "Joe", start = false, daemon = true, logging = false)(())
      assert(thread.getName == "Joe")
      assert(thread.isDaemon)
      assert(!thread.isAlive)
   }
