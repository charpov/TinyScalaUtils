package tinyscalautils.test.mixins

import org.scalatest.{ Failed, Outcome, TestSuite, TestSuiteMixin }
import tinyscalautils.lang.StackOverflowException

/** A mixin that transforms `StackOverflowError`s into corresponding `StackOverflowException`s.
  *
  * The ScalaTest framework dies on `StackOverflowError`s, but can handle
  * [[tinyscalautils.lang.StackOverflowException]]. Trait [[NoStackOverflowError]] can be used to
  * replace `StackOverflowError`s with instances of this class.
  *
  * @since 1.0
  */
trait NoStackOverflowError extends TestSuiteMixin:
   self: TestSuite =>

   abstract override def withFixture(test: NoArgTest): Outcome =
      super.withFixture(test) match
         case Failed(e: StackOverflowError) => Failed(StackOverflowException(e))
         case other                         => other
