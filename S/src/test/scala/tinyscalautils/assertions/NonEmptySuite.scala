package tinyscalautils.assertions

import org.scalatest.funsuite.AnyFunSuite

class NonEmptySuite extends AnyFunSuite:

   test("nonEmpty") {
      val col = java.util.HashSet[Int]()
      assert(!col.nonEmpty)
      col.add(0)
      assert(col.nonEmpty)
   }
