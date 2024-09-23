package tinyscalautils.util

import org.scalatest.funsuite.AnyFunSuite

class IsZeroSuite extends AnyFunSuite:
   test("isZero Byte"):
      val n: Byte = 0
      assert(n.isZero && !(n + 1).isZero)

   test("isZero Char"):
      val n: Char = 0
      assert(n.isZero && !(n + 1).isZero)

   test("isZero Short"):
      val n: Short = 0
      assert(n.isZero && !(n + 1).isZero)

   test("isZero Int"):
      val n: Int = 0
      assert(n.isZero && !(n + 1).isZero)

   test("isZero Long"):
      val n: Long = 0
      assert(n.isZero && !(n + 1).isZero)

   test("isZero Float"):
      val n: Float = 0
      assert(n.isZero && !(n + 1).isZero)

   test("isZero Double"):
      val n: Double = 0
      assert(n.isZero && !(n + 1).isZero)

   test("isZero BigInt"):
      val n: BigInt = 0
      assert(n.isZero && !(n + 1).isZero)

   test("isZero BigDecimal"):
      val n: BigDecimal = 0
      assert(n.isZero && !(n + 1).isZero)

   test("isZero"):
      given Numeric[String] with
         def fromInt(x: Int): String                  = ".".repeat(x)
         def minus(x: String, y: String): String      = ???
         def negate(x: String): String                = ???
         def parseString(str: String): Option[String] = ???
         def plus(x: String, y: String): String       = ???
         def times(x: String, y: String): String      = ???
         def toDouble(x: String): Double              = ???
         def toFloat(x: String): Float                = ???
         def toInt(x: String): Int                    = x.length
         def toLong(x: String): Long                  = ???
         def compare(x: String, y: String): Int       = x.compareTo(y)
      val n: String = ""
      assert(n.isZero && !(n + 1).isZero)
