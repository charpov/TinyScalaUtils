package tinyscalautils.util

import scala.util.Random
import tinyscalautils.assertions.require

extension (rand: Random)
   /** Adds a Kotlin-like `nextInt` method to random number generators. This method enables calls of
     * the form `rand.nextInt(1 to 100)`.
     *
     * This method is only defined for ranges. For a more general sequence of integers, use
     * `pickOne` instead.
     *
     * Range argument cannot be empty.
     */
   def nextInt(range: Range): Int =
      require(range.nonEmpty, "range is empty")
      range(rand.nextInt(range.length))
