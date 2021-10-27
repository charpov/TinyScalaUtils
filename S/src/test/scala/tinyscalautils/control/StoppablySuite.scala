package tinyscalautils.control

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.{ Executors, newThread, withLocalContext }
import tinyscalautils.timing.{ timeOf, sleep }
import org.scalactic.Tolerance.convertNumericToPlusOrMinusWrapper

import java.util.concurrent.{ CountDownLatch, ExecutorService }
import java.util.logging.{ Level, Logger }
import scala.concurrent.Future
import scala.concurrent.duration.SECONDS

class StoppablySuite extends AnyFunSuite:

   Logger.getLogger("tinyscalautils.threads").setLevel(Level.WARNING)

   test("stoppably") {
      val done   = CountDownLatch(2)
      var runner = Option.empty[Thread]
      var ex     = Option.empty[Exception]
      val thread = newThread() {
         try
            stoppably(2.0) {
               runner = Some(Thread.currentThread)
               done.countDown()
               while true do ()
            }
         catch
            case e: Exception =>
               ex = Some(e)
               done.countDown()
      }
      val time = timeOf {
         thread.interrupt()
         done.await()
         runner.get.join()
      }
      assert(time === 2.0 +- 0.1)
      assert(ex.get.isInstanceOf[InterruptedException])
   }
