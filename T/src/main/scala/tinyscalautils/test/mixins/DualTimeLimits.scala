package tinyscalautils.test.mixins

import org.scalatest.concurrent.TimeLimitedTests
import org.scalatest.tagobjects.Slow
import org.scalatest.time.Span
import org.scalatest.{ Outcome, TestSuite }

/** An extension of `TimeLimitedTests` that uses the `Slow` tag to set a higher time limit for slow
  * tests.
  *
  * @see
  *   [[org.scalatest.concurrent.TimeLimitedTests]]
  * @see
  *   [[org.scalatest.tagobjects.Slow]]
  *
  * @since 1.0
  */
trait DualTimeLimits extends TimeLimitedTests:
   self: TestSuite =>

   /** Time limit for regular (short) tests.
     *
     * @group Time
     * limits
     */
   def shortTimeLimit: Span

   /** Time limit for slow tests. Slow tests are annotated with [[org.scalatest.tagobjects.Slow]].
     * @group Time
     * limits
     */
   def longTimeLimit: Span

   private var currentTestIsLong: Boolean = false

   /** Time limit.
     * @return
     *   `shortTimeLimit` or `longTimeLimit` based on tags.
     */
   final def timeLimit: Span = if currentTestIsLong then longTimeLimit else shortTimeLimit

   abstract override def withFixture(test: NoArgTest): Outcome =
      if !test.tags(Slow.name) then super.withFixture(test)
      else
         try
            currentTestIsLong = true
            super.withFixture(test)
         finally currentTestIsLong = false
