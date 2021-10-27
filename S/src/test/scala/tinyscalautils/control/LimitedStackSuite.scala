package tinyscalautils.control

import org.scalatest.funsuite.AnyFunSuite

import scala.util.Try
import tinyscalautils.lang.StackOverflowException

class LimitedStackSuite extends AnyFunSuite:

   def f(x: Int): Int = 1 + f(x + 1)

   test("limitedStack") {
      assertThrows[StackOverflowError] {
         val t = Try(f(0)) // dies with StackOverflowError, no usable t value
      }
      val t = Try(limitedStack(f(0)))
      assert {
         t.isFailure // true
      }
      assert {
         t.failed.get.isInstanceOf[StackOverflowException] // true
      }
   }
