package tinyscalautils.text

import org.scalatest.funsuite.AnyFunSuite

class PluralSuite extends AnyFunSuite:
   test("plural, specified"):
      assert(plural(0.5, "mouse", "mice") == "mouse")
      assert(plural(1.5, "mouse", "mice") == "mice")

   test("plural, guessed"):
      for (s, p) <- Seq(
           ("dog", "dogs"),
           ("CAT", "CATS"),
           ("platypus", "platypuses"),
           ("CAMPUS", "CAMPUSES")
         )
      do
         assert(plural(0.5, s) == s)
         assert(plural(1.5, s) == p)

   test("plural, BigInt"):
      assert(plural(BigInt(10), "day") == "days")
