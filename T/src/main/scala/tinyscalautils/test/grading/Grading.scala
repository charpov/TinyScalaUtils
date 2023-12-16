package tinyscalautils.test.grading

import org.scalatest.Suite
import tinyscalautils.test.mixins.Reporting

/** A special case of `Reporting` suite in which the reporter is an instance of `Grader`.
  *
  * @since 1.0
  */
trait Grading extends Reporting:
   self: Suite =>

   override val reporter: Grader = Grader()

   /** The reporter, as a grader. */
   def grader: Grader = reporter
