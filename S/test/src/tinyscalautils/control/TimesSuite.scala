package tinyscalautils.control

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.lang.unit

class TimesSuite extends AnyFunSuite:
   test("times"):
      var count = 0

      0 times (count += 1)
      assert(count == 0)

      10 times (count += 1)
      assert(count == 10)

      5 times (count -= 1)
      assert(count == 5)

   test("times, exception"):
      assertThrows[IllegalArgumentException](-1 times unit)
