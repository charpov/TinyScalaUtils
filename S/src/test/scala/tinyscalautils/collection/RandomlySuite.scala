package tinyscalautils.collection

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.control.times
import tinyscalautils.util.FastRandom

import scala.util.Random

class RandomlySuite extends AnyFunSuite:
   given Random = FastRandom

   test("randomly") {
      val n = 100
      val m = 1000
      class Count(var c: Int = 0)
      val strings          = Set.range(0, n).map(_.toString)
      val counts           = strings.map(str => (str, Count())).toMap
      def done             = counts.valuesIterator.forall(_.c >= m)
      def add(str: String) = counts(str).c += 1
      val iter             = strings.randomly
      (n * m) times add(iter.next())
      while !done do add(iter.next())
   }

   test("empty") {
      assert(!Iterable.empty.randomly.hasNext)
   }
