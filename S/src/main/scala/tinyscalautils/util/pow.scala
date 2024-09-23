package tinyscalautils.util

import tinyscalautils.assertions.require

import scala.compiletime.asMatchable
import scala.math.Numeric.Implicits.infixNumericOps

extension [A: Numeric](n: A)
   /** Calculates the interger power of a number.
     *
     * @note
     *   This function return 1 when `m` is 0, even if `n` is zero.
     *
     * @throws IllegalArgumentException
     *   if the power is negative.
     *
     * @since 1.5.0
     */
   def pow(m: Int): A =
      require(m >= 0, s"pow argument cannot be negative, is $m")
      n.asMatchable match
         case bigInt: BigInt         => bigInt.pow(m).asInstanceOf[A]
         case bigDecimal: BigDecimal => bigDecimal.pow(m).asInstanceOf[A]
         case _ =>
            var p    = Numeric[A].one
            var base = n
            var exp  = m
            while exp > 0 do
               if (exp & 1) != 0 then p *= base
               base *= base
               exp >>= 1
            p
