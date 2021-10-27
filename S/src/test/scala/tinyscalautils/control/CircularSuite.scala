package tinyscalautils.control

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow

class CircularSuite extends AnyFunSuite:

   test("circular") {
      val i: Iterator[Int] = List(1, 2, 3).circular
      assert(i.take(10).sameElements(List(1, 2, 3, 1, 2, 3, 1, 2, 3, 1)))
   }

   test("circular, empty") {
      assert(Nil.circular.isEmpty)
   }

   test("circular, large", Slow) {
      val n = 1_000_000_000
      val i = List("X").circular
      assert(i.take(n).sameElements(Iterator.fill(n)("X")))
   }
