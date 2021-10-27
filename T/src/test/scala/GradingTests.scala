import org.scalactic.{Prettifier, SizeLimit}
import org.scalatest.Args
import org.scalatest.exceptions.TestFailedException
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.lang.unit
import tinyscalautils.test.mixins.GradingRun

class GradingTests extends AnyFunSuite:

   private val nums = List.range(0,42)

   test("prettifier and grade") {
      class Tests extends AnyFunSuite with GradingRun:
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
      class Tests extends AnyFunSuite with GradingRun:
         override val prettifier = Prettifier.truncateAt(SizeLimit(10))

         test("overridden truncating prettifier") {
            val e = intercept[TestFailedException](assert(nums.isEmpty))
            assert(e.message.exists(_.endsWith(", 9, ...) was not empty")))
         }

      val suite = Tests()
      suite.run(None, Args(_ => unit))
      assert(suite.grader.grade == 1.0)
   }

   test("super overridden prettifier and grade") {
      val Foo = 0
      class Tests extends AnyFunSuite with GradingRun:
         override val prettifier = Prettifier {
            case Foo => "zero"
            case o => super.prettifier(o)
         }

         test("overridden truncating prettifier") {
            val e1 = intercept[TestFailedException](assert(nums.isEmpty))
            assert(e1.message.exists(_.endsWith(", 31, ...) was not empty")))
            val e2 = intercept[TestFailedException](assert(Foo == 1))
            assert(e2.message.contains("zero did not equal 1"))
         }

      val suite = Tests()
      suite.run(None, Args(_ => unit))
      assert(suite.grader.grade == 1.0)
   }
