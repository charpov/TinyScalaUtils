package tinyscalautils.util

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.control.times

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

   test("isPowerOf2 Int"):
      assertThrows[IllegalArgumentException](-1.isPowerOf2)
      assert(!3.isPowerOf2)
      assert(!5.isPowerOf2)
      assert(!6.isPowerOf2)
      assert(!7.isPowerOf2)
      var ok = true
      var n  = 1073741825
      while n < 2147483647 do
         ok &&= !n.isPowerOf2
         n += 1
      assert(ok)

      n = 1
      31 times:
         assert(n.isPowerOf2)
         n <<= 1

   test("isPowerOf2 Long"):
      assertThrows[IllegalArgumentException](-1L.isPowerOf2)
      assert(!3L.isPowerOf2)
      assert(!5L.isPowerOf2)
      assert(!6L.isPowerOf2)
      assert(!7L.isPowerOf2)
      var ok = true
      var n  = 2147483649L
      while n <= 4294967295L do
         ok &&= !n.isPowerOf2
         n += 1

      assert(ok)

      n = 1L
      63 times:
         assert(n.isPowerOf2)
         n <<= 1
