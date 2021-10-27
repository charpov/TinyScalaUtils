package tinyscalautils.control

import tinyscalautils.assertions.require

extension [U](number: Int)
   /** Simple repetitions.
     *
     * Evaluates an expression a given number of times. e.g.:
     *
     * {{{
     * 42 times {
     *     ...
     * }
     * }}}
     *
     * Throws [[IllegalArgumentException]] if the target number is negative.
     *
     * @since 1.0
     */
   @throws[IllegalArgumentException]("if the target number is negative")
   infix def times(code: => U): Unit =
      require(number >= 0, s"target number $number is negative")

      var i = 0
      while i < number do
         i += 1
         code
   end times
