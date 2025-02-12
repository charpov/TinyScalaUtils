package tinyscalautils.util

import tinyscalautils.assertions.require

import scala.math.Fractional.Implicits.infixFractionalOps

/** Calculates an average by ignoring a fixed number of low/high values. The number is specified as
  * a number of ''pairs'' lowest/highest values.
  *
  * This function returns 0 if the sequence is empty, or all the numbers are ignored.
  */
def average[A : Fractional](seq: Seq[A], ignoredPairs: Int = 0): A =
   require(ignoredPairs >= 0, s"ignoredPairs argument must be non-negative, not $ignoredPairs")
   val len = seq.length
   if ignoredPairs * 2 >= len then Fractional[A].zero
   else
      seq.sorted.view.slice(ignoredPairs, len - ignoredPairs).sum /
         Fractional[A].fromInt(len - ignoredPairs * 2)
