package tinyscalautils.util

import tinyscalautils.assertions.require

/** The largest integer `m` such that `2^m <= n`.
  *
  * @throws IllegalArgumentException
  *   if n is not positive.
  */
def log2(n: Int): Int =
   require(n > 0, s"n must be positive, not $n")
   (java.lang.Integer.SIZE - 1) - java.lang.Integer.numberOfLeadingZeros(n)

/** The largest integer `m` such that `2^m <= n`.
  *
  * @throws IllegalArgumentException
  *   if n is not positive.
  */
def log2(n: Long): Int =
   require(n > 0L, s"n must be positive, not $n")
   (java.lang.Long.SIZE - 1) - java.lang.Long.numberOfLeadingZeros(n)

extension (n: Int)
   /** True if `n` is a power of two.
     *
     * @throws IllegalArgumentException
     *   if n is not positive.
     */
   def isPowerOf2: Boolean =
      require(n > 0, s"n must be positive, not $n")
      (n & n - 1) == 0

extension (n: Long)
   /** True if `n` is a power of two.
     *
     * @throws IllegalArgumentException
     *   if n is not positive.
     */
   def isPowerOf2: Boolean =
      require(n > 0, s"n must be positive, not $n")
      (n & n - 1) == 0
