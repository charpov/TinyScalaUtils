package tinyscalautils.timing

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
