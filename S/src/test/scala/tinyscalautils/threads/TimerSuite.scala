package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.timing.{sleep, timeOf, zipWithDuration}

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

class TimerSuite extends AnyFunSuite with Tolerance:

   test("schedule") {
      val timer = Executors.newTimer(1)
      withThreadPoolAndWait(timer, true) {
         val latch  = CountDownLatch(1)
         val future = timer.schedule(1.5)(latch.countDown()).zipWithDuration
         assert(timeOf(latch.await()) === 1.5 +- 0.1)
         future.map((_, time) => assert(time === 1.5 +- 0.1))
      }
   }

   test("Future") {
      withThreadPoolAndWait(Executors.newTimer(1), true) {
         val latch  = CountDownLatch(1)
         val future = DelayedFuture(1.5)(latch.countDown()).zipWithDuration
         assert(timeOf(latch.await()) === 1.5 +- 0.1)
         future.map((_, time) => assert(time === 1.5 +- 0.1))
      }
   }

   test("repeat") {
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
   }
