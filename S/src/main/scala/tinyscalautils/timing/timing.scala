package tinyscalautils.timing

import scala.concurrent.{ ExecutionContext, Future }

/** An alias for `System.nanoTime`.
  *
  * @since 1.0
  */
//noinspection AccessorLikeMethodIsEmptyParen
inline def getTime(): Long = System.nanoTime()

/** An alias for `System.currentTimeMillis`.
  *
  * @since 1.0
  */
inline def now(): Long = System.currentTimeMillis()

/** Executes code with timing.
  *
  * @return
  *   the value produced by `code`, and a timing in seconds.
  *
  * @since 1.0
  */
def timeIt[A](code: => A): (A, Double) =
   val start = getTime()
   val value = code
   val nanos = getTime() - start
   (value, nanos / 1E9)

/** Executes code with timing.
  *
  * @return
  *   a timing for `code`, in seconds.
  *
  * @since 1.0
  */
def timeOf[U](code: => U): Double =
   val start = getTime()
   code
   val nanos = getTime() - start
   nanos / 1E9

extension [A](future: Future[A])
   /** Adds duration (in seconds) to a future. Duration is calculated from the invocation of this
     * method to the completion of the future.
     *
     * @return
     *   a future of a pair (value, duration)
     *
     * @since 1.0
     */
   def zipWithDuration(using ExecutionContext): Future[(A, Double)] =
      val start = getTime()
      future.map { value =>
         val end = getTime()
         value -> (end - start) / 1E9
      }
end extension

extension (seconds: Double)
   /** Multiplies the double value by 1e9 then rounds. */
   def toNanos: Long = (seconds * 1E9).round
