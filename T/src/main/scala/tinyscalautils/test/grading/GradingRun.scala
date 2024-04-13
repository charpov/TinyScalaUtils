package tinyscalautils.test.grading

import org.scalatest.concurrent.{ Signaler, ThreadSignaler }
import org.scalatest.time.Span
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.{ Canceled, Failed, Outcome, TestSuite }
import tinyscalautils.test.mixins.{ DualTimeLimits, NoStackOverflowError }
import tinyscalautils.test.tagobjects.{ Async, Fail }
import tinyscalautils.threads.Executors.global
import tinyscalautils.threads.runAsync

/** Setup for a grading run. This trait sets time limits (1 second and 1 minute, to be overridden
  * for customization), catches `StackOverflowError`, detects a `Fail` tag to fail a test manually,
  * and relies on `Async` tags to run tests in separate (interruptible) threads.
  *
  * @see
  *   [[tinyscalautils.test.mixins.DualTimeLimits]]
  *
  * @see
  *   [[tinyscalautils.test.tagobjects.Async]]
  *
  * @see
  *   [[tinyscalautils.test.tagobjects.Fail]]
  *
  * @since 1.0
  */
trait GradingRun extends Grading, DualTimeLimits, NoStackOverflowError:
   self: TestSuite =>

   override val defaultTestSignaler: Signaler = ThreadSignaler

   val shortTimeLimit: Span = 1.second
   val longTimeLimit: Span  = 1.minute

   abstract override def withFixture(test: NoArgTest): Outcome =
      val failed = test.configMap.getWithDefault[Set[String]]("failed", Set.empty)
      if failed(test.name) then Failed(s"test name in failed set")
      else if test.tags.isEmpty then super.withFixture(test) // no tag (fast path)
      else
         val failTags = test.tags.filter(_.startsWith(Fail.name))
         if failTags.size > 1 then Canceled(s"""conflicting tags: ${failTags.mkString(", ")}""")
         else if failTags.nonEmpty then // Fail tag
            val failTag = failTags.head
            Fail.regex.findFirstMatchIn(failTag) match
               case None => Canceled(s"'$failTag' is not a valid tag")
               case Some(m) =>
                  val message = m.group(1)
                  if message eq null then Failed() else Failed(message)
         else if test.tags.contains(Async.name) then // Async tag
            val newTest =
               new NoArgTest:
                  def apply(): Outcome = runAsync(test.apply())(using global)

                  val configMap = test.configMap
                  val name      = test.name
                  val scopes    = test.scopes
                  val text      = test.text
                  val tags      = test.tags
                  val pos       = test.pos
            super.withFixture(newTest)
         else super.withFixture(test) // other tags
