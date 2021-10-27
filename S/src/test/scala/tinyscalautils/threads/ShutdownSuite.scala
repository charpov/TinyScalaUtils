package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.timing.{ sleep, timeIt }
import tinyscalautils.threads.shutdownAndWait

class ShutdownSuite extends AnyFunSuite with Tolerance:

   test("shutdownAndWait, true") {
      val exec = Executors.newThreadPool(4)
      exec.execute((() => sleep(.5)): Runnable)
      assert(exec.shutdownAndWait(1.0))
   }

   test("shutdownAndWait, false") {
      val exec = Executors.newThreadPool(4)
      exec.execute((() => sleep(2.0)): Runnable)
      val (terminated, time) = timeIt(exec.shutdownAndWait(1.0))
      assert(!terminated)
      assert(time === 1.0 +- 0.1)
      sleep(0.5)
      assert(!exec.isTerminated)
      assert(exec.shutdownAndWait(1.0))
   }

   test("shutdownAndWait, false, force") {
      val exec = Executors.newThreadPool(4)
      exec.execute((() => sleep(2.0)): Runnable)
      val (terminated1, time1) = timeIt(exec.shutdownAndWait(1.0, force = true))
      assert(!terminated1)
      assert(time1 === 1.0 +- 0.1)
      val (terminated2, time2) = timeIt(exec.shutdownAndWait(1.0))
      assert(terminated2)
      assert(time2 < 0.1)
   }
