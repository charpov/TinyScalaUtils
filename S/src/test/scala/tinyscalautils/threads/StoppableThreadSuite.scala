package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.assertions.implies
import tinyscalautils.timing.timeOf

import java.util.logging.{ Level, Logger }
import scala.concurrent.duration.SECONDS

class StoppableThreadSuite extends AnyFunSuite with Tolerance:

   private val logger = Logger.getLogger("tinyscalautils.threads")
   logger.setLevel(Level.WARNING)

   def loop(responsive: Boolean): Runnable =
      () => while responsive implies Thread.interrupted() do ()

   test("logging") {
      // this test should be silent
      try
         logger.setLevel(Level.INFO)
         val thread = StoppableThread(loop(responsive = false), 0.0, logging = false)
         thread.start()
         thread.interrupt()
         thread.join()
      finally logger.setLevel(Level.WARNING)
   }

   test("stopping, default delay") {
      val thread = StoppableThread(loop(responsive = false))
      thread.start()
      thread.interrupt()
      val time = timeOf(thread.join())
      assert(time === 1.0 +- 0.1)
   }

   test("stopping, 2-second delay") {
      val thread = StoppableThread(loop(responsive = false), 2.0)
      thread.start()
      thread.interrupt()
      val time = timeOf(thread.join())
      assert(time === 2.0 +- 0.1)
   }

   test("interrupting only") {
      val thread = StoppableThread(loop(responsive = true), 3.0)
      thread.start()
      thread.interrupt()
      val time = timeOf(thread.join())
      assert(time === 0.0 +- 0.1)
   }

   test("all 3 tests, but using newStoppableThread method") {
      val thread1 = newStoppableThread()(loop(responsive = true).run())
      thread1.interrupt()
      assert(timeOf(thread1.join()) === 0.0 +- 0.1)
      val thread2 = newStoppableThread()(loop(responsive = false).run())
      thread2.interrupt()
      assert(timeOf(thread2.join()) === 1.0 +- 0.1)
      val thread3 = newStoppableThread(2.0)(loop(responsive = false).run())
      thread3.interrupt()
      assert(timeOf(thread3.join()) === 2.0 +- 0.1)
   }
