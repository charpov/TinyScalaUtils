package tinyscalautils.util

import org.scalatest.funsuite.AnyFunSuite

class Log2Suite extends AnyFunSuite:
   test("log2 Int"):
      assertThrows[IllegalArgumentException](log2(0))
      assert(log2(1) == 0)
      assert(log2(2) == 1)
      assert(log2(3) == 1)
      assert(log2(7) == 2)
      assert(log2(8) == 3)
      assert(log2(-1 >>> 2) == 29)
      assert(log2(1 << 30) == 30)

   test("log2 Long"):
      assertThrows[IllegalArgumentException](log2(0L))
      assert(log2(1L) == 0)
      assert(log2(2L) == 1)
      assert(log2(3L) == 1)
      assert(log2(7L) == 2)
      assert(log2(8L) == 3)
      assert(log2(-1L >>> 2) == 61)
      assert(log2(1L << 62) == 62)
