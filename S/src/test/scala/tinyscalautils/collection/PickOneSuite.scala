package tinyscalautils.collection

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.timing.timeOf
import tinyscalautils.util.FastRandom

import scala.util.Random

class PickOneSuite extends AnyFunSuite:

   val seq  = IndexedSeq.tabulate(1_000_000)(_.toString)
   val list = seq.toList

   given Random = FastRandom

   test("pickOne") {
      assert(seq.pickOne(using FastRandom(1)) == "372926")
      assert(list.pickOne(using FastRandom(1)) == "372926")
      assertThrows[NoSuchElementException](IndexedSeq.empty.pickOne)
      assertThrows[NoSuchElementException](List.empty.pickOne)
   }

   test("pickOneOption") {
      assert(seq.pickOneOption(using FastRandom(1)) contains "372926")
      assert(list.pickOneOption(using FastRandom(1)) contains "372926")
      assert(IndexedSeq.empty.pickOneOption.isEmpty)
      assert(List.empty.pickOneOption.isEmpty)
   }

   test("random") {
      val x = seq.pickOne
      while seq.pickOne == x do ()
   }

   test("timing") {
      val t1 = timeOf(seq.pickOne)
      val t2 = timeOf(list.pickOne)
      assert(t2 / t1 > 10.0)
   }
