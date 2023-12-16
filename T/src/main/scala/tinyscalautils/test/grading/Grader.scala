package tinyscalautils.test.grading

import org.scalatest.*
import org.scalatest.events.*

/** A test reporter that counts passing and failing tests into a grade. Ignored tests are ignored
  * (i.e., not passed or failed and do not affect the grade).
  *
  * Tests can have an optional weight, specified between square brackets as follows:
  * {{{
  * test("this is a test [2]") {...}
  * }}}
  *
  * or
  *
  * {{{
  * test("a big test [10pts]") {...}
  * }}}
  *
  * The default weight is 1.
  *
  * Instances of this class are not thread-safe.
  *
  * @see
  *   [[org.scalatest.Reporter]]
  *
  * @since 1.0
  */
class Grader extends Reporter:
   import Grader.weight

   private var sumWeight, sumPassed, w = 0.0
   private var tests                   = 0

   /** The grade.  This is the weighted ratio of tests passed over tests failed. */
   def grade: Double = sumPassed / sumWeight

   /** Total weight.  This is the sum of the weights of all the tests. */
   def totalWeight: Double = sumWeight

   /** Total number of tests that were run.
     *
     * @since 1.1
     */
   def testCount: Int = tests

   /** Processes an event.
     *
     * `TestStarting`, `TestSucceeded` and `TestFailed` are used to keep track of the grade. Other
     * events are ignored.
     * @see
     *   [[org.scalatest.events.Event]]
     */
   def apply(event: Event): Unit =
      event match
         case e: TestStarting  => w = weight(e.testName); tests += 1
         case _: TestSucceeded => sumWeight += w; sumPassed += w
         case _: TestFailed    => sumWeight += w
         case _                => () // do nothing
end Grader

/** Companion object of [[package.Grader]]. */
private object Grader:
   private val weightRegex = """\[\s*(\d*\.?\d+)\s*\p{Alpha}*\]""".r.unanchored

   private def weight(name: String): Double = weightRegex.findAllMatchIn(name).toSeq match
      case Seq() => 1.0
      case s     => s.last.group(1).toDoubleOption.getOrElse(1.0)
end Grader
