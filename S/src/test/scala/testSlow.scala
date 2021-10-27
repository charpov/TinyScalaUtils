import tinyscalautils.timing.{ delay, timeOf, sleep, SlowIterator }
import tinyscalautils.control.times
import scala.concurrent.duration.NANOSECONDS

@main def testSlow(): Unit =
   def time[A](i: Iterator[A]) =
      println {
         timeOf {
            while i.hasNext do i.next()
         }
      }
   val l     = List.fill(1_000_000)("X")
   val steps = 1
   time {
      l.iterator
         .take(0)
         .slow(1 - .000000001, steps)
   }
   time {
      l.iterator
         .take(0)
         .slow(1 + .000000001)
   }
