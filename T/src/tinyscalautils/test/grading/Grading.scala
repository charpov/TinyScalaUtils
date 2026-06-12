package tinyscalautils.test.grading

import org.scalactic.source.Position
import org.scalatest.*
import org.scalatest.concurrent.{ Signaler, ThreadSignaler }
import org.scalatest.time.Span
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import tinyscalautils.test.mixins.{ DualTimeLimits, NoStackOverflowError, Reporting }
import tinyscalautils.test.tagobjects.{ Async, Fail }
import tinyscalautils.threads.Executors.global
import tinyscalautils.threads.runAsync

/** Setup for a grading run.
  *
  * This trait sets default time limits for fast and slow tests (1 second and 1 minute, to be
  * overridden for customization), catches `StackOverflowError`, detects a `Fail` tag to fail a test
  * manually, fails all tests in the `failed` set from the config map, and relies on `Async` tags to
  * run tests in separate (interruptible) threads.
  *
  * The trait also counts passing and failing tests into a grade. Ignored tests are ignored (i.e.,
  * not passed or failed and do not affect the grade).
  *
  * Tests can have an optional weight, specified as follows:
  * {{{
  *    test("this is a test", 2.points) {...}
  * }}}
  *
  * or
  *
  * {{{
  *    test("this is a test", 2.pts) {...}
  * }}}
  *
  * or
  *
  * {{{
  *    test("this is a test", Points(2)) {...}
  * }}}
  *
  * or
  *
  * {{{
  *    test("this is a test [2 points]") {...}
  * }}}
  *
  * or
  *
  * {{{
  *    test("a big test [2pts]") {...}
  * }}}
  *
  * or ...
  *
  * When not specified, the default weight is 1.
  *
  * @see
  *   [[tinyscalautils.lang.StackOverflowException]]
  * @see
  *   [[tinyscalautils.test.mixins.DualTimeLimits]]
  * @see
  *   [[tinyscalautils.test.tagobjects.Timeout]]
  * @see
  *   [[tinyscalautils.test.tagobjects.Async]]
  * @see
  *   [[tinyscalautils.test.tagobjects.Fail]]
  *
  * @since 1.0
  */
trait Grading(weight: Int = 0)
    extends GradingSuite,
      Reporting,
      BeforeAndAfterAll,
      DualTimeLimits,
      NoStackOverflowError:
   self: TestSuite =>

   protected val reporter: Grader & Reporter = WeightedGrader(weight.toDouble)

   def grader: Grader = reporter

   override def beforeAll(): Unit = grader.asInstanceOf[WeightedGrader].setTags(tags)

   override val defaultTestSignaler: Signaler = ThreadSignaler

   val shortTimeLimit: Span = 1.second
   val longTimeLimit: Span  = 1.minute

   private def shouldFail(testName: String, failed: Set[String]): Boolean =
      failed.exists:
         case s""""$str"""" => testName == str
         case regex         => regex.r.matches(testName)

   abstract override def withFixture(test: NoArgTest): Outcome =
      val failed = test.configMap.getWithDefault[Set[String]]("failed", Set.empty)
      if shouldFail(test.name, failed) then Failed("test name in 'failed' set")
      else if test.tags.isEmpty then super.withFixture(test) // no tag (fast path)
      else
         val failTags = test.tags.filter(_.startsWith(Fail.name))
         if failTags.size > 1 then Canceled(s"""conflicting tags: ${failTags.mkString(", ")}""")
         else if failTags.nonEmpty then // Fail tag
            val failTag = failTags.head
            Fail.regex.findFirstMatchIn(failTag) match
               case None    => Canceled(s"'$failTag' is not a valid tag")
               case Some(m) =>
                  val message = m.group(1)
                  if message eq null then Failed() else Failed(message)
         else if test.tags.contains(Async.name) then // Async tag
            val newTest =
               new NoArgTest:
                  def apply(): Outcome = runAsync(test.apply())(using global)

                  val configMap: ConfigMap       = test.configMap
                  val name: String               = test.name
                  val scopes: IndexedSeq[String] = test.scopes
                  val text: String               = test.text
                  val tags: Set[String]          = test.tags
                  val pos: Option[Position]      = test.pos
            super.withFixture(newTest)
         else super.withFixture(test) // other tags
end Grading
