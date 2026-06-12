package tinyscalautils.collection

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.util.FastRandom

class AllDistinctSuite extends AnyFunSuite:
   test("small"):
      assert(Seq.empty.allDistinct)
      assert(Seq(1).allDistinct)
      assert(Seq(1, 2).allDistinct)
      assert(!Seq(1, 1).allDistinct)
      assert(Seq(1, 2, 3).allDistinct)
      assert(!Seq(1, 2, 1).allDistinct)
      assert(Seq(1, 2, 3, 4).allDistinct)
      assert(!Seq(1, 2, 3, 2).allDistinct)

   test("large"):
      val n    = 1000
      val nums = Set.fill(n)(FastRandom.nextInt()).toIndexedSeq
      val one  = nums.pickOne(using FastRandom)
      assert(nums.allDistinct)
      assert(Range(0, n).count(i => nums.updated(i, one).allDistinct) == 1)
