package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.timing.{ sleep, timeIt, timeOf, zipWithDuration }

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.Future
import scala.util.Using

class TimerSuite extends AnyFunSuite with Tolerance:
   test("schedule"):
      val timer = Executors.newTimer(1)
      withThreads(timer, true):
         val latch  = CountDownLatch(1)
         val future = timer.schedule(1.5)(latch.countDown()).zipWithDuration
         assert(timeOf(latch.await()) === 1.5 +- 0.1)
         future.map((_, time) => assert(time === 1.5 +- 0.1))

   test("DelayedFuture"):
      withThreads(Executors.newTimer(1), true):
         val latch  = CountDownLatch(1)
         val future = DelayedFuture(1.5)(latch.countDown()).zipWithDuration
         assert(timeOf(latch.await()) === 1.5 +- 0.1)
         future.map((_, time) => assert(time === 1.5 +- 0.1))

   test("Execute"):
      withThreads(Executors.newTimer(1), true):
         val latch = CountDownLatch(1)
         ExecuteAfter(1.5)(latch.countDown())
         assert(timeOf(latch.await()) === 1.5 +- 0.1)
         Future.unit

   test("repeat"):
      val timer   = Executors.newTimer(2)
      val repeats = AtomicInteger()
      val latch   = CountDownLatch(1)
      timer.schedule(2.5)(latch.countDown())
      timer.scheduleAtFixedRate(0.0, 1.0)(repeats.incrementAndGet())
      timer.scheduleWithFixedDelay(0.0, 1.0)(repeats.incrementAndGet())
      sleep(1.5)
      timer.shutdown()
      assert(timeOf(latch.await()) === 1.0 +- 0.1)
      assert(repeats.get == 4)

   test("shutdown"):
      val (thread, time) = timeIt:
         withThreads():
            val timer = Executors.newTimer(1)
            val f     = timer.schedule(1.5)(Thread.currentThread)
            timer.shutdown()
            f
      assert(time === 1.5 +- 0.1)
      assert(thread.joined(1.0))

   test("close"):
      val (thread, time) = timeIt:
         withThreads():
            Using.resource(Executors.newTimer(1))(_.schedule(1.5)(Thread.currentThread))
      assert(time === 1.5 +- 0.1)
      assert(thread.joined(1.0))
