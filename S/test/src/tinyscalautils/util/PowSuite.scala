package tinyscalautils.util

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite

import java.math.MathContext
import scala.annotation.experimental

@experimental
class PowSuite extends AnyFunSuite with Tolerance:
   import scala.language.experimental.genericNumberLiterals

   private val BigPower = 100_000

   test("pow (1)"):
      assertThrows[IllegalArgumentException](2.pow(-1))
      assert(0.pow(0) == 1)
      assert(0.pow(1) == 0)
      assert(1.pow(0) == 1)
      assert(1.pow(BigPower) == 1)
      assert(2.pow(0) == 1)
      assert(2.pow(10) == 1024)
      assert(2.pow(11) == 2048)
      assert(2L.pow(0) == 1L)
      assert(2L.pow(30) == 1073741824L)
      assert(2L.pow(29) == 536870912L)
      assert(2L.pow(60) == 1152921504606846976L)
      assert(2L.pow(59) == 576460752303423488L)
      assert(3.pow(10) == 59049)
      assert(3L.pow(30) == 205891132094649L)
      assert(1.2.pow(10) === 6.1917364224 +- 1E-5)

   test("pow (2)"):
      val n: BigInt = 12345678987654321
      assert(pow(n)(0) == 1)
      assert(pow(n)(10) == n.pow(10))
      assert(pow(n)(BigPower) == n.pow(BigPower))

   test("pow (3)"):
      val d: BigDecimal = BigDecimal(123456789.87654321, MathContext.UNLIMITED)
      assert(pow(d)(0) == 1)
      assert(pow(d)(10) == d.pow(10))
      assert(pow(d)(BigPower) == d.pow(BigPower))

   test("pow (4)"):
      def ref(n: Long, m: Int) = math.pow(n.toDouble, m.toDouble).round
      for (n, max) <- Seq((0, 10), (1, 10), (2, 30)); m <- 0 to max do assert(n.pow(m) == ref(n, m))
      for m <- 0 to 62 do assert(2L.pow(m) == ref(2L, m))
