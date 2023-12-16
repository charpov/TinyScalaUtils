import org.scalatest.Args
import org.scalatest.events.TestFailed
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.test.assertions.assertExpr

class AssertionsTests extends AnyFunSuite:

   test("assertExpr") {
      class Tests extends AnyFunSuite:
         test("success") {
            assertExpr(Seq(1,2)) {
               List(1) ::: List(2)
            }
         }
         test("failed") {
            assertExpr(42, ": not good") {
               40 + 1
            }
         }
      end Tests

      val suite = Tests()
      assert(suite.run(Some("success"), silent).succeeds())
      assert(!suite.run(Some("failed"), Args(R)).succeeds())
      R.lastEvent match
         case Some(ev: TestFailed) => assert(ev.message == "Expected 41, but got 42 : not good")
         case other => fail(s"unexpected: $other ")
   }
