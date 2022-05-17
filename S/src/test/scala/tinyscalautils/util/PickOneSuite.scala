package tinyscalautils.util

import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random
import tinyscalautils.timing.timeOf

class PickOneSuite extends AnyFunSuite:

   val seq  = IndexedSeq.tabulate(1_000_000)(_.toString)
   val list = seq.toList

   given Random = Random

   test("pickOne") {
      assert(seq.pickOne(using Random(1)) == "548985")
      assert(list.pickOne(using Random(1)) == "548985")
      assertThrows[NoSuchElementException](IndexedSeq.empty.pickOne)
      assertThrows[NoSuchElementException](List.empty.pickOne)
   }

   test("pickOneOption") {
      assert(seq.pickOneOption(using Random(1)) contains "548985")
      assert(list.pickOneOption(using Random(1)) contains "548985")
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
