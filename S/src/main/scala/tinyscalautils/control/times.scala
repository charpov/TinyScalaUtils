package tinyscalautils.control

import tinyscalautils.assertions.require

extension (number: Int)
   /** Simple repetitions.
     *
     * Evaluates an expression a given number of times. e.g.:
     *
     * {{{
     * 42 times:
     *     ...
     * }}}
     *
     * Throws [[IllegalArgumentException]] if the target number is negative.
     *
     * @since 1.0
     */
   @throws[IllegalArgumentException]("if the target number is negative")
   infix inline def times(inline code: Any): Unit =
      require(number >= 0, s"target number $number is negative")
      var i = number
      while i > 0 do
         i -= 1
         code
