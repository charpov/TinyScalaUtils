package tinyscalautils.util

import org.scalatest.funsuite.AnyFunSuite

class EvenOddSuite extends AnyFunSuite:
   test("Int"):
      assert(42.isEven)
      assert(-42.isEven)
      assert(!41.isEven)
      assert(!(-41.isEven))
      assert(!42.isOdd)
      assert(!(-42.isOdd))
      assert(41.isOdd)
      assert(-41.isOdd)

   test("Long"):
      assert(42L.isEven)
      assert(-42L.isEven)
      assert(!41L.isEven)
      assert(!(-41L.isEven))
      assert(!42L.isOdd)
      assert(!(-42L.isOdd))
      assert(41L.isOdd)
      assert(-41L.isOdd)
