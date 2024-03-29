package tinyscalautils.util

import tinyscalautils.assertions.require

/** The largest integer `m` such that `2^m <= n`.
  *
  * @throws IllegalArgumentException
  *   if n is not positive.
  */
def log2(n: Int): Int =
   require(n > 0, "n must be positive")

   var log = 0
   var m   = n
   while m > 1 do
      log += 1
      m = m >> 1
   log

/** The largest integer `m` such that `2^m <= n`.
  *
  * @throws IllegalArgumentException
  *   if n is not positive.
  */
def log2(n: Long): Int =
   require(n > 0L, "n must be positive")

   var log = 0
   var m   = n
   while m > 1L do
      log += 1
      m = m >> 1
   log
