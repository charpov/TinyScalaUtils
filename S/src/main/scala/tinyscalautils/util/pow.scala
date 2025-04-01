package tinyscalautils.util

import tinyscalautils.assertions.require

import scala.compiletime.asMatchable
import scala.math.Numeric.Implicits.infixNumericOps

extension [A: Numeric](n: A)
   /** Calculates the interger power of a number.
     *
     * @note
     *   This function does not check for overflows; it also return 1 when `m` is 0, even if `n` is
     *   zero.
     *
     * @throws IllegalArgumentException
     *   if the power is negative.
     *
     * @since 1.5.0
     */
   def pow(m: Int): A =
      require(m >= 0, s"pow argument must be non-negative, not $m")

      val numeric      = Numeric[A]
      def pow2(m: Int) = numeric.fromInt(1 << m)
      def times2(x: A) = x * numeric.fromInt(2)

      n.asMatchable match
         case _ if m == 0                       => numeric.one
         case 0                                 => numeric.zero
         case 1                                 => numeric.one
         case 2: Int                            => pow2(m)
         case 2: Long if m < 31                 => pow2(m)
         case 2: Long if m < 63 && (m & 1) == 0 => pow2(m / 2).pow(2)
         case 2: Long if m < 63                 => times2(pow2(m / 2).pow(2))
         case bigInt: BigInt                    => bigInt.pow(m).asInstanceOf[A]
         case bigDecimal: BigDecimal            => bigDecimal.pow(m).asInstanceOf[A]
         case _ =>
            var p    = numeric.one
            var base = n
            var exp  = m
            while exp > 0 do
               if (exp & 1) != 0 then p *= base
               base *= base
               exp >>= 1
            p
