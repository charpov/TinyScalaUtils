package tinyscalautils.util

import scala.math.Numeric
import scala.compiletime.asMatchable
import scala.math.Numeric.Implicits.infixNumericOps

extension [Num: Numeric](n: Num)
   /** Test that a number is zero. */
   inline def isZero: Boolean = inline n.asMatchable match
      case byte: Byte             => byte == 0
      case char: Char             => char == 0
      case short: Short           => short == 0
      case int: Int               => int == 0
      case long: Long             => long == 0
      case float: Float           => float == 0
      case double: Double         => double == 0
      case bigInt: BigInt         => bigInt.signum == 0
      case bidDecimal: BigDecimal => bidDecimal.signum == 0
      case _                      => n.sign.toInt == 0
