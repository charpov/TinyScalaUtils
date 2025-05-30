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
     * @throws IllegalArgumentException if the target number is negative.
     *
     * @since 1.0
     */
   infix inline def times(inline code: Any): Unit =
      require(number >= 0, s"target number must be non-negative, not $number")
      var i = number
      while i > 0 do
         i -= 1
         code
