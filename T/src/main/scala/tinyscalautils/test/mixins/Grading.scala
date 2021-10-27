package tinyscalautils.test.mixins

import org.scalatest.{ Reporter, Suite }
import tinyscalautils.test.grading.Grader

/** A special case of `Reporting` suite in which the reporter is an instance of `Grader`.
  *
  * @since 1.0
  */
trait Grading extends Reporting:
   self: Suite =>

   override val reporter: Grader = Grader()

   /** The reporter, as a grader. */
   def grader: Grader = reporter
