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
      def invalid(tag: String): Nothing =
         throw IllegalArgumentException(s"'$tag' is not a valid tag")

      def conflict(multi: Seq[(String, Any)]): Nothing =
         throw IllegalArgumentException(s"conflicting tags: ${multi.map(_._1).mkString(", ")}")

      try
         currentTimeLimit =
            if test.tags(NoTimeout.name) then Span.Max
            else
               test.tags.toSeq
                  .flatMap:
                     case Slow.name => Some(Slow.name -> longTimeLimit)
                     case tag @ s"$name($str)" if name == Timeout.name =>
                        str.toDoubleOption
                           .filter(_ > 0.0)
                           .map(secs => tag -> secs.seconds) orElse invalid(tag)
                     case tag if tag.startsWith(Timeout.name) => invalid(tag)
                     case _                                   => None
                  .match
                     case Seq((_, timeout)) => timeout
                     case Seq()             => shortTimeLimit
                     case multi             => conflict(multi)
      catch case e: IllegalArgumentException => return Canceled(e.getMessage)
      super.withFixture(test)
