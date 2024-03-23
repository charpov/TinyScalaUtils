package tinyscalautils.timing

import org.scalactic.{ Equality, Tolerance }
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow
import tinyscalautils.timing.*

import scala.compiletime.asMatchable
import scala.io.Source

@org.scalatest.Ignore
// Note: This suite takes 50 minutes and 40 seconds to run.
class SlowSuite extends AnyFunSuite with Tolerance:
   private given Equality[Double] with
      def areEqual(x: Double, value: Any): Boolean = value.asMatchable match
         case y: Double => x === y +- y / 10.0
         case _         => false

   private def newlines = new Iterator[Char]:
      def hasNext = true
      def next()  = '\n'

   private def consume(iterator: Iterator[Char]) =
      timeOf(while iterator.hasNext do iterator.next())

   for
      time   <- Seq(1.0, 5.0, 10.0, 60.0)
      length <- Seq(0, 1, 10, 100)
      nano   <- Seq(1E-9, -1E-9)
      steps  <- Seq(1, 10, 100, 1000, 10000)
   do
      test(s"slow iterator ${(time, length, nano, steps)}", Slow):
         assert(consume(newlines.take(length).slow(time + nano, steps)) === time)

   test("slow source", Slow):
      val source = Source
         .fromIterable:
            new Iterable[Char]:
               def iterator = newlines.take(1000)
         .slow(5.0)
      assert(timeOf(source.mkString) === 5.0)
      assert(timeOf(source.reset().mkString) === 5.0)

   test("slow stream", Slow):
      val stream = newlines.take(100).to(LazyList).slow(5.0)
      assert(timeOf(stream.last) === 5.0)
      val fast = timeOf:
         assert(stream.nonEmpty)
         assert(stream.head == '\n')
         assert(stream.take(10).toSet == Set('\n'))
         assert(stream.last == '\n')
         assert(stream.size == 100)
      assert(fast < 0.1)

   test("slow stream ending (implementation dependent)", Slow):
      val stream = newlines.take(10).to(LazyList).slow(5.0, delayedElements = 20)
      val time1  = timeOf(stream(9))
      val time2  = timeOf(stream.last)
      assert(time1 === 2.5)
      assert(time1 + time2 === 5.0)
