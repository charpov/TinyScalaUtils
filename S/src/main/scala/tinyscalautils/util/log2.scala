package tinyscalautils.util

import tinyscalautils.assertions.require

/** The largest integer `m` such that `2^m <= n`.
  *
  * @throws IllegalArgumentException
  *   if n is not positive.
  */
def log2(n: Int): Int =
   require(n > 0, "n must be positive")
   31 - Integer.numberOfLeadingZeros(n)

/** The largest integer `m` such that `2^m <= n`.
  *
  * @throws IllegalArgumentException
  *   if n is not positive.
  */
def log2(n: Long): Int =
   require(n > 0L, "n must be positive")
   63 - java.lang.Long.numberOfLeadingZeros(n)
