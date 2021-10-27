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

      require(count == 0, s"${incr()}")
      assert(count == 0)

      val ex = intercept[IllegalArgumentException] {
         require(count == 1, s"${incr()}")
      }
      assert(count == 1)
      assert(ex.getMessage == "1")
   }

   test("require lazy message 2") {
      var count = 0
      def incr() =
         count += 1
         count

      require(count == 0, "%d%d", incr(), incr())
      assert(count == 0)

      val ex = intercept[IllegalArgumentException] {
         require(count == 1, "%d%d", incr(), incr())
      }
      assert(count == 2)
      assert(ex.getMessage == "12")
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

      requireState(count == 0, s"${incr()}")
      assert(count == 0)

      val ex = intercept[IllegalStateException] {
         requireState(count == 1, s"${incr()}")
      }
      assert(count == 1)
      assert(ex.getMessage == "1")
   }

   test("requireState lazy message 2") {
      var count = 0
      def incr() =
         count += 1
         count

      requireState(count == 0, "%d%d", incr(), incr())
      assert(count == 0)

      val ex = intercept[IllegalStateException] {
         requireState(count == 1, "%d%d", incr(), incr())
      }
      assert(count == 2)
      assert(ex.getMessage == "12")
   }
