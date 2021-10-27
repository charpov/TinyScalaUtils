package tinyscalautils.assertions

import org.scalatest.funsuite.AnyFunSuite

class RequirementsSuite extends AnyFunSuite:

   test("require basic") {
      require(true)
      val ex = intercept[IllegalArgumentException] {
         require(false)
      }
      assert(ex.getMessage eq null)
   }

   test("require lazy message 1") {
      var count = 0
      def incr() =
         count += 1
         count

      require(true, s"${incr()}")
      assert(count == 0)

      val ex = intercept[IllegalArgumentException] {
         require(false, s"${incr()}")
      }
      assert(count == 1)
      assert(ex.getMessage == "1")
   }

   test("require lazy message 2") {
      var count = 0
      def incr() =
         count += 1
         count

      require(true, "%d", incr())
      assert(count == 0)

      val ex = intercept[IllegalArgumentException] {
         require(false, "%d", incr())
      }
      assert(count == 1)
      assert(ex.getMessage == "1")
   }

   test("requireState basic") {
      requireState(true)
      val ex = intercept[IllegalStateException] {
         requireState(false)
      }
      assert(ex.getMessage eq null)
   }

   test("requireState lazy message 1") {
      var count = 0
      def incr() =
         count += 1
         count

      requireState(true, s"${incr()}")
      assert(count == 0)

      val ex = intercept[IllegalStateException] {
         requireState(false, s"${incr()}")
      }
      assert(count == 1)
      assert(ex.getMessage == "1")
   }

   test("requireState lazy message 2") {
      var count = 0
      def incr() =
         count += 1
         count

      requireState(true, "%d", incr())
      assert(count == 0)

      val ex = intercept[IllegalStateException] {
         requireState(false, "%d", incr())
      }
      assert(count == 1)
      assert(ex.getMessage == "1")
   }
