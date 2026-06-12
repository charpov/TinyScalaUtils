package tinyscalautils.test.mixins

import org.scalatest.{ Outcome, TestSuite, TestSuiteMixin }

/** Makes the suite silent. All stdout output is ignored. Stderr is maintained.
  *
  * @since 1.0
  */
trait SilentTests extends TestSuiteMixin:
   self: TestSuite =>
   abstract override def withFixture(test: NoArgTest): Outcome =
      Console.withOut(java.io.OutputStream.nullOutputStream)(super.withFixture(test))
