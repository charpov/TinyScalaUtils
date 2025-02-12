package tinyscalautils.collection

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.util.FastRandom

class SortedSuite extends AnyFunSuite:
   test("in reverse 1"):
      val l            = List.fill(10)(FastRandom.nextInt())
      val s: List[Int] = l.sortedInReverse
      assert(s == l.sorted.reverse)

   test("in reverse 2"):
      val l                  = IndexedSeq.fill(10)(FastRandom.nextInt())
      val s: IndexedSeq[Int] = l.sortedInReverse
      assert(s == l.sorted.reverse)

   test("in reverse custom"):
      val l = Seq("CCC", "ZZ", "A")
      assert(l.sortedInReverse == Seq("ZZ", "CCC", "A"))
      assertResult(l):
         given Ordering[String] = Ordering.by(_.length)
         l.sortedInReverse
