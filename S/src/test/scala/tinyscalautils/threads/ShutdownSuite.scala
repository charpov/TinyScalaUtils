package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.timing.{ sleep, timeIt }
import tinyscalautils.threads.shutdownAndWait

class ShutdownSuite extends AnyFunSuite with Tolerance:

   for (timeout <- Seq(1.0, 0.1, 10.0)) do
      test(s"awaitTermination (time = $timeout)") {
         val exec = Executors.newThreadPool(4)
         exec.run(sleep(0.5))
         exec.shutdown()
         val (terminated, time) = timeIt {
            if timeout < 10.0 then exec.awaitTermination(timeout) else exec.awaitTermination()
         }
         assert(terminated == timeout > 0.5)
         assert(time === timeout.min(0.5) +- 0.1)
         assert(exec.awaitTermination())
      }

      test(s"shutdownAndWait (time = $timeout)") {
         val exec = Executors.newThreadPool(4)
         exec.run(sleep(0.5))
         val (terminated, time) = timeIt {
            if timeout < 10.0 then exec.shutdownAndWait(timeout) else exec.shutdownAndWait()
         }
         assert(terminated == timeout > 0.5)
         assert(time === timeout.min(0.5) +- 0.1)
         assert(exec.awaitTermination())
      }

   test("shutdownAndWait, force") {
      val exec = Executors.newThreadPool(4)
      exec.run(sleep(1.0))
      val (terminated1, time1) = timeIt(exec.shutdownAndWait(0.5, force = true))
      assert(!terminated1)
      assert(time1 === 0.5 +- 0.1)
      val (terminated2, time2) = timeIt(exec.awaitTermination())
      assert(terminated2)
      assert(time2 === 0.0 +- 0.1)
   }
