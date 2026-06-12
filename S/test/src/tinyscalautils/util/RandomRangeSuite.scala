package tinyscalautils.util

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.collection.in

import scala.collection.mutable
import scala.util.chaining.scalaUtilChainingOps

class RandomRangeSuite extends AnyFunSuite:
   test("nextInt"):
      for range <- Seq(0 until 1, 1 to 10, 100 until 200) do
         val set = mutable.BitSet.fromSpecific(range)
         while set.nonEmpty do set -= FastRandom.nextInt(range).tap(n => assert(n in range))

   test("empty range"):
      assertThrows[IllegalArgumentException](FastRandom.nextInt(1 to 0))
