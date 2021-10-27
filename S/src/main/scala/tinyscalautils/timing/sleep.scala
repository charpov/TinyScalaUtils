package tinyscalautils.timing

import scala.concurrent.duration.NANOSECONDS
import tinyscalautils.assertions.*
import scala.io.Source

/** Limit below which sleeping is replaced with spinning.
  *
  * Increasing/decreasing it can help with precision/performance.
  */
inline private val SpinningNanos = 2_000_000L

/** When using a series of delays to slow down streams, minimum amount of time to accumulate before
  * calling `sleep`.
  */
inline private val MinSleepNanos = 100_000_000L

private def delayNanos(nanos: Long, start: Long = getTime()): Unit =
   val end = start + nanos
   try
      var time = getTime()
      while end - time > 0 do
         val sleepTime = (end - time) / 2
         if sleepTime > SpinningNanos then NANOSECONDS.sleep(sleepTime)
         time = getTime()
   catch case _: InterruptedException => Thread.currentThread.interrupt()

/** Adds sleep time so code takes up specified duration.
  *
  * An optional argument can be used to specify the starting clock, as per [[getTime]]; this is
  * useful when preliminary computation needs to be performed before sleeping.
  *
  * This method does not throw `InterruptedException`. If the thread is interrupted, the sleeping
  * stops and the thread is left interrupted.
  *
  * @param start
  *   A starting point for sleep time, as per [[getTime]].
  *
  * @since 1.0
  */
def delay[A](seconds: Double, start: Long = getTime())(code: => A): A =
   val value = code
   delayNanos((seconds * 1E9).round, start)
   value

/** Pauses the calling thread for the specified amount of time.
  *
  * This method differs from `Thread.sleep` is several ways:
  *   - durations are specified in seconds as a floating point number.
  *   - the method never undershoots, as it sometimes happens with `Thread.sleep` on some platforms.
  *   - `InterruptedException` is not thrown; the thread is left interrupted instead.
  *
  * @param start
  *   A starting point for sleep time, as per [[getTime]].
  *
  * @since 1.0
  */
def sleep(seconds: Double, start: Long = getTime()): Unit = delayNanos((seconds * 1E9).round, start)

/** Adds a `slow` method to iterators. */
// The given is needed because, as of 3.1, Scala cannot handle multiple extensions by the same name
// if they have default argument values.
given SlowIterator: AnyRef with
   extension [A](source: Iterator[A])
      /** Slows down the iterator.
        *
        * This method returns an iterator in which method `next` invokes `sleep` to slow it down.
        * The number of elements being slowed down can be chosen: more means a smoother iterator,
        * less means fewer calls to `sleep` overall. If the iterator has more elements than the
        * specified number, the remaining elements are produced without delay. If the iterator has
        * fewer elements, method `hasNext` is delayed before returning `false` at the end of the
        * iteration. Overall, stepping through the entire iterator adds the entire specified delay
        * (assuming the iterator does end). The default value 32 is entirely arbitrary.
        *
        * The time taken by the underlying iterator to produce its values is not taken into account.
        * Delays are added to that time. Therefore, stepping through the entire iterator takes more
        * time than the specified delay.
        *
        * The purpose of this method is to slow down demos. The actual time taken by an iterator to
        * produce all its values is not guaranteed to be very precise (i.e., in the order of
        * seconds, not milliseconds).
        */
      def slow(seconds: Double, delayedElements: Int = 32): Iterator[A] =
         require(delayedElements > 0)

         var remaining = (seconds * 1E9).round
         val delta     = remaining / delayedElements
         var delay     = 0L

         new Iterator[A]() {
            def hasNext = source.hasNext || (remaining > 0) && {
               delayNanos(remaining)
               remaining = 0L
               false
            }

            def next() =
               if remaining > 0 then
                  delay += delta
                  if delay > MinSleepNanos then
                     delayNanos(delay min remaining)
                     remaining -= delay
                     delay = 0L
               source.next()
         }
      end slow

/** Adds a `slow` method to sources. */
// The given is needed because, as of 3.1, Scala cannot handle multiple extensions by the same name
// if they have default argument values.
given SlowSource: AnyRef with
   extension (source: Source)
      /** Slows down the source.
        *
        * This method returns a source in which method `next` invokes `sleep` to slow it down. The
        * number of characters being slowed down can be chosen: more means a smoother source, less
        * means fewer calls to `sleep` overall. If the source has more characters than the specified
        * number, the remaining characters are produced without delay. If the source has fewer
        * characters, method `hasNext` is delayed before returning `false` at the end of the
        * iteration. Overall, stepping through the entire source adds the entire specified delay.
        * The default value 1024 is entirely arbitrary.
        *
        * The time taken by the underlying source to produce its characters is not taken into
        * account. Delays are added to that time. Therefore, stepping through the entire source
        * takes more time than the specified delay.
        *
        * The purpose of this method is to slow down demos. The actual time taken by a source to
        * produce all its characters is not guaranteed to be very precise (i.e., in the order of
        * seconds, not milliseconds).
        */
      def slow(seconds: Double, delayedCharacters: Int = 1024): Source =
         require(delayedCharacters > 0)

         new Source() {
            val iter = (source: Iterator[Char]).slow(seconds, delayedCharacters)
         } withReset (() => source.reset().slow(seconds, delayedCharacters))
      end slow

/** Adds a `slow` method to streams. */
// The given is needed because, as of 3.1, Scala cannot handle multiple extensions by the same name
// if they have default argument values.
given SlowLazyList: AnyRef with
   extension [A](source: LazyList[A])
      /** Slows down the stream.
        *
        * This method returns a stream in which creation of elements invokes `sleep` to slow it
        * down. The number of elements being slowed down can be chosen: more means a smoother
        * stream, less means fewer calls to `sleep` overall. If the stream has more elements than
        * the specified number, the remaining elements are produced without delay. If the stream has
        * fewer elements, methods that finish the stream (e.g., `size`, `last`, `toList`, etc.) are
        * delayed at the end of the stream. Overall, stepping through the entire stream (and knowing
        * that the entire stream has been processed) adds the entire specified delay (assuming the
        * stream does end). The default value 32 is entirely arbitrary.
        *
        * The time taken by the underlying stream to produce its values is not taken into account.
        * Delays are added to that time. Therefore, stepping through the entire stream takes more
        * time than the specified delay.
        *
        * The purpose of this method is to slow down demos. The actual time taken by a stream to
        * produce all its values is not guaranteed to be very precise (i.e., in the order of
        * seconds, not milliseconds).
        */
      def slow(seconds: Double, delayedElements: Int = 32): LazyList[A] =
         require(delayedElements > 0)
         source.iterator.slow(seconds, delayedElements).to(LazyList)
      end slow
