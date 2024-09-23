package tinyscalautils.util

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable
import tinyscalautils.collection.in

class RandomRangeSuite extends AnyFunSuite:
   test("nextInt"):
      for range <- Seq(0 until 1, 1 to 10, 100 until 200) do
         val set = mutable.BitSet.fromSpecific(range)
         while set.nonEmpty do
            val n = FastRandom.nextInt(range)
            assert(n in range)
            set -= n

   test("empty range"):
      assertThrows[IllegalArgumentException](FastRandom.nextInt(1 to 0))
