package tinyscalautils.collection

import org.scalatest.funsuite.AnyFunSuite

class LastSuite extends AnyFunSuite:
   test("last"):
      assert(Iterator(1).last == 1)
      assert(Iterator(1, 2, 3).last == 3)
      assertThrows[NoSuchElementException](Iterator.empty.last)

   test("lastOption"):
      assert(Iterator(1).lastOption.contains(1))
      assert(Iterator(1, 2, 3).lastOption.contains(3))
      assert(Iterator.empty.lastOption.isEmpty)
