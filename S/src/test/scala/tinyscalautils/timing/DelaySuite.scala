package tinyscalautils.timing

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.{ KeepThreadsFactory, withLocalThreadPool }
import tinyscalautils.timing.{ delay, sleep, timeOf }

import java.util.concurrent.atomic.{ AtomicInteger, AtomicReference }
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future, TimeoutException }

class DelaySuite extends AnyFunSuite with Tolerance:

   private def slowTrue(millis: Long) =
      Thread.sleep(millis)
      true

   test("delay, start") {
      val (v, t) = timeIt {
         val start = getTime()
         slowTrue(1000)
         delay(1.75, start)(slowTrue(500))
      }
      assert(v)
      assert(t === 1.75 +- 0.1)
   }

   test("sleep, start") {
      val t = timeOf {
         val start = getTime()
         slowTrue(1000)
         sleep(1.75, start)
      }
      assert(t === 1.75 +- 0.1)
   }

   test("delay, slow") {
      val (v, t) = timeIt(delay(1.75)(slowTrue(1500)))
      assert(v)
      assert(t === 1.75 +- 0.1)
   }

   test("sleep") {
      val t = timeOf(sleep(1.75))
      assert(t === 1.75 +- 0.1)
   }

   test("delay, fast") {
      val (v, t) = timeIt(delay(1.75)(true))
      assert(v)
      assert(t === 1.75 +- 0.1)
   }

   test("interrupt") {
      var time = 0.0
      var flag = false
      val task: Runnable = () =>
         time = timeOf(sleep(1.75))
         flag = Thread.interrupted()
      val thread = Thread(task)
      thread.start()
      Thread.sleep(1000)
      thread.interrupt()
      thread.join()
      assert(time === 1.0 +- 0.1)
      assert(flag)
   }
