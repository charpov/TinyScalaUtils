package tinyscalautils.test.mixins

import org.scalatest.concurrent.TimeLimitedTests
import org.scalatest.tagobjects.Slow
import org.scalatest.time.Span
import org.scalatest.time.SpanSugar.convertDoubleToGrainOfTime
import org.scalatest.{ Canceled, Outcome, TestSuite }
import tinyscalautils.test.tagobjects.{ NoTimeout, Timeout }

import scala.compiletime.uninitialized

/** An extension of `TimeLimitedTests` that uses the `Fast`, `Slow` and `Timeout` tags to set a
  * higher time limit for slow tests.
  *
  * @see
  *   [[org.scalatest.concurrent.TimeLimitedTests]]
  *
  * @see
  *   [[org.scalatest.tagobjects.Slow]]
  *
  * @see
  *   [[tinyscalautils.test.tagobjects.Timeout]]
  *
  * @since 1.0
  */
trait DualTimeLimits extends TimeLimitedTests:
   self: TestSuite =>

   /** Time limit for regular (short) tests.
     *
     * @group Time limits
     */
   def shortTimeLimit: Span

   /** Time limit for slow tests. Slow tests are annotated with [[org.scalatest.tagobjects.Slow]].
     * @group Time limits
     */
   def longTimeLimit: Span

   private var currentTimeLimit: Span = uninitialized

   /** Time limit.
     * @return
     *   `shortTimeLimit` or `longTimeLimit` or custom time limit, based on tags.
     */
   final def timeLimit: Span = currentTimeLimit

   abstract override def withFixture(test: NoArgTest): Outcome =
      currentTimeLimit = shortTimeLimit // default
      if test.tags.nonEmpty then
         val tags = test.tags.filter: str =>
            str == Slow.name || str == NoTimeout.name || str.startsWith(Timeout.name)
         if tags.size > 1 then return Canceled(s"""conflicting tags: ${tags.mkString(", ")}""")
         if tags.nonEmpty then
            val tag = tags.head
            if tag == Slow.name then currentTimeLimit = longTimeLimit
            else if tag == NoTimeout.name then currentTimeLimit = Span.Max
            else // Timeout
               Timeout.regex.findFirstMatchIn(tag).flatMap(_.group(1).toDoubleOption) match
                  case Some(value) if value > 0.0 => currentTimeLimit = value.seconds
                  case _                          => return Canceled(s"'$tag' is not a valid tag")
      super.withFixture(test)
