package tinyscalautils.java

import java.util.function.Supplier

private final class TimingScala:

   def getTime(): Long = tinyscalautils.timing.getTime()

   def now(): Long = tinyscalautils.timing.now()

   def timeOf(code: Runnable): Double =
      tinyscalautils.timing.timeOf(code.run())

   def timeIt[A](code: Supplier[? <: A]): TimingPair[A] =
      val (value, time) = tinyscalautils.timing.timeIt(code.get())
      TimingPair(value, time)

   def delay[A](seconds: Double, start: Long, code: Supplier[? <: A]): A =
      tinyscalautils.timing.delay(seconds, start)(code.get())

   def delay[A](seconds: Double, code: Supplier[? <: A]): A =
      tinyscalautils.timing.delay(seconds)(code.get())

   def sleep(seconds: Double, start: Long): Unit =
      tinyscalautils.timing.sleep(seconds, start)

   def sleep(seconds: Double): Unit =
      tinyscalautils.timing.sleep(seconds)
