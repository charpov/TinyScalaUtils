import org.scalactic.{ Prettifier, SizeLimit }
import org.scalatest.Args
import org.scalatest.exceptions.TestFailedException
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.test.mixins.Grading
import tinyscalautils.lang.unit

class GradingTests extends AnyFunSuite:

   private val nums = List.tabulate(42)(identity)

   test("prettifier and grade") {
      class Tests extends AnyFunSuite with Grading:
         test("default truncating prettifier") {
            val e = intercept[TestFailedException](assert(nums.isEmpty))
            assert(e.message.exists(_.endsWith(", 31, ...) was not empty")))
         }

         test("truncating prettifier") {
            given Prettifier = Prettifier.truncateAt(SizeLimit(10))

            val e = intercept[TestFailedException](assert(nums.isEmpty))
            assert(e.message.exists(_.endsWith(", 9, ...) was not empty")))
         }

         test("failed [3pts]") {
            fail()
         }
      end Tests

      val suite = Tests()
      suite.run(None, Args(_ => unit))
      assert(suite.grader.grade == 0.4)
   }

   test("overridden prettifier and grade") {
      class Tests extends AnyFunSuite with Grading:
         override val prettifier = Prettifier.truncateAt(SizeLimit(10))

         test("overridden truncating prettifier") {
            val e = intercept[TestFailedException](assert(nums.isEmpty))
            assert(e.message.exists(_.endsWith(", 9, ...) was not empty")))
         }

      val suite = Tests()
      suite.run(None, Args(_ => unit))
      assert(suite.grader.grade == 1.0)
   }
