package tinyscalautils.test.mixins

import org.scalatest.TestSuite
import org.scalatest.concurrent.{ Signaler, ThreadSignaler }
import org.scalatest.time.Span
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import tinyscalautils.threads.{ StoppableThread, timeoutTimer }

import java.util.concurrent.ThreadFactory

/** Setup for a grading run. This trait sets time limits (1 second and 1 minute, to be overridden
  * for customization), catches `StackOverflowError`, and sets a runner factory with stoppable
  * threads.
  *
  * @since 1.0
  */
trait GradingRun extends Grading, DualTimeLimits, NoStackOverflowError, RunnerFactory:
   self: TestSuite =>

   override val defaultTestSignaler: Signaler = ThreadSignaler

   val runnerFactory: ThreadFactory = new StoppableThread(_, 5.0)

   given ThreadFactory = runnerFactory

   val shortTimeLimit: Span = 1.second
   val longTimeLimit: Span  = 1.minute
