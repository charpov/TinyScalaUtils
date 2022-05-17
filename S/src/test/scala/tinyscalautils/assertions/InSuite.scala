package tinyscalautils.assertions

import org.scalatest.funsuite.AnyFunSuite

class InSuite extends AnyFunSuite:

   test("seq") {
      assert(2 in Seq(1, 2, 3))
      assert(2 in Seq(1, 2, true))
      assert(!(0 in Seq(1, 2, 3)))
      assert(!(0 in Seq(1, 2, true)))
   }

   test("range") {
      assert(2 in (1 to 10))
      assert(!(0 in (1 to 10)))
   }

   test("set") {
      assert(2 in Set(1, 2, 3))
      assert(2 in Set(1, 2, true))
      assert(!(0 in Set(1, 2, 3)))
      assert(!(0 in Set(1, 2, true)))
   }

   test("map") {
      assert(2 in Map(1 -> "A", 2 -> "B", 3 -> "C"))
      assert(2 in Map(1 -> "A", 2 -> "B", true -> false))
      assert(!(0 in Map(1 -> "A", 2 -> "B", 3 -> "C")))
      assert(!(0 in Map(1 -> "A", 2 -> "B", true -> false)))
   }

   test("option") {
      assert(2 in Some(2))
      assert(2 in Some[AnyVal](2))
      assert(!(2 in None))
   }

   test("iterable") {
      val col = new Iterable[Int] {
         def iterator: Iterator[Int] = Iterator(1, 2, 3)
      }
      assert(2 in col)
      assert(!(0 in col))
   }
