package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.newThread
import tinyscalautils.timing.{ getTime, sleep }

class SpinningSuite extends AnyFunSuite with Tolerance:
   test("isSpinning"):
      val t = newThread:
         val end = getTime() + 3E9
         while getTime() < end do sleep(0.1)

      assert(t.isSpinning)
      assert(!t.isSpinning(threshold = 0.1))
      assert(t.isSpinning(seconds = 0.5, threshold = 0.001))
      assert(!t.isSpinning)
      assert(!t.isSpinning)
