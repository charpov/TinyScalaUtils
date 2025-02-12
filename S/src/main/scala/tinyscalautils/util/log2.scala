package tinyscalautils.util

import tinyscalautils.assertions.require

/** The largest integer `m` such that `2^m <= n`.
  *
  * @throws IllegalArgumentException
  *   if n is not positive.
  */
def log2(n: Int): Int =
   require(n > 0, s"n must be positive, not $n")
   31 - java.lang.Integer.numberOfLeadingZeros(n)

/** The largest integer `m` such that `2^m <= n`.
  *
  * @throws IllegalArgumentException
  *   if n is not positive.
  */
def log2(n: Long): Int =
   require(n > 0L, s"n must be positive, not $n")
   63 - java.lang.Long.numberOfLeadingZeros(n)
