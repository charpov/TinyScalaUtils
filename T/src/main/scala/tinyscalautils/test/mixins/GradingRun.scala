package tinyscalautils.test.mixins

import org.scalactic.{Prettifier, SizeLimit}
import org.scalatest.TestSuite
import org.scalatest.concurrent.{Signaler, ThreadSignaler}
import org.scalatest.time.Span
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime

import java.util.concurrent.ThreadFactory

/** Setup for a grading run. This trait sets time limits (1 second and 1 minute, to be overridden
  * for customization), and catches `StackOverflowError`.
  *
  * @since 1.0
  */
trait GradingRun extends Grading, DualTimeLimits, NoStackOverflowError:
   self: TestSuite =>

   override val defaultTestSignaler: Signaler = ThreadSignaler

   val shortTimeLimit: Span = 1.second
   val longTimeLimit: Span  = 1.minute

   /** Default prettifier, which truncates collections after 32 elements. */
   protected def prettifier: Prettifier = Prettifier.truncateAt(SizeLimit(32))

   given Prettifier = prettifier
