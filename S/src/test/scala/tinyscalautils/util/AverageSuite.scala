package tinyscalautils.util

import org.scalatest.funsuite.AnyFunSuite

import scala.math.BigDecimal.double2bigDecimal

class AverageSuite extends AnyFunSuite:

   val nums: Seq[BigDecimal] = Seq(1, 7, 9, 11, 12, 14)

   test("average") {
      assert(average(nums) == 9)
      assert(average(nums, 1) == 9.75)
      assert(average(nums, 2) == 10)
      assert(average(nums, 3) == 0)
      assert(average(nums, 4) == 0)
      assertThrows[IllegalArgumentException](average(nums, -1))
   }
