package tinyscalautils.util

import org.scalatest.funsuite.AnyFunSuite

class Log2Suite extends AnyFunSuite:

   test("log2") {
      assertThrows[IllegalArgumentException](log2(0))
      assert(log2(1) == 0)
      assert(log2(2) == 1)
      assert(log2(3) == 1)
      assert(log2(7) == 2)
      assert(log2(8) == 3)
      assert(log2(-1 >>> 2) == 29)
      assert(log2(1 << 30) == 30)
   }
