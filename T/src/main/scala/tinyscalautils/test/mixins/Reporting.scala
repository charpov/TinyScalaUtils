package tinyscalautils.test.mixins

import org.scalatest.*
import org.scalatest.events.*

/** Adds a reporter to the test suite. This reporter is invoked first, followed by the other
  * reporters specified in method `run`.
  *
  * @since 1.0
  */
trait Reporting extends SuiteMixin:
   self: Suite =>

   /** A new reporter, invoked first. */
   protected val reporter: Reporter

   abstract override def run(testName: Option[String], args: Args): Status =
      val r = new Reporter:
         def apply(event: Event) =
            try reporter(event)
            finally args.reporter(event)
      super.run(testName, args.copy(reporter = r))
end Reporting
