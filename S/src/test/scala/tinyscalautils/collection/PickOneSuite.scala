package tinyscalautils.collection

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.lang.unit
import tinyscalautils.timing.timeOf
import tinyscalautils.util.FastRandom

import scala.collection.mutable
import scala.util.Random
import scala.util.chaining.scalaUtilChainingOps

class PickOneSuite extends AnyFunSuite:
   private val seq  = IndexedSeq.tabulate(1_000_000)(_.toString)
   private val list = seq.toList

   given Random = FastRandom

   test("pickOne"):
      assert(seq.pickOne(using FastRandom(1)) == "372926")
      assert(list.pickOne(using FastRandom(1)) == "372926")
      assertThrows[NoSuchElementException](IndexedSeq.empty.pickOne)
      assertThrows[NoSuchElementException](List.empty.pickOne)

   test("pickOneOption"):
      assert(seq.pickOneOption(using FastRandom(1)).contains("372926"))
      assert(list.pickOneOption(using FastRandom(1)).contains("372926"))
      assert(IndexedSeq.empty.pickOneOption.isEmpty)
      assert(List.empty.pickOneOption.isEmpty)

   test("random"):
      val x = seq.pickOne
      while seq.pickOne == x do unit

   test("timing"):
      val t1 = timeOf(seq.pickOne)
      val t2 = timeOf(list.pickOne)
      assert(t2 / t1 > 10.0)

   test("ranges"):
      // This is the same test as in util.RandomRangeSuite
      for range <- Seq(0 until 1, 1 to 10, 100 until 200) do
         val set = mutable.BitSet.fromSpecific(range)
         while set.nonEmpty do set -= range.pickOne.tap(n => assert(n in range))
