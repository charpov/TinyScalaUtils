package tinyscalautils.test.mixins

import org.scalatest.{ Reporter, Suite }
import tinyscalautils.test.grading.Grader
import org.scalactic.{ Prettifier, SizeLimit }

/** A special case of `Reporting` suite in which the reporter is an instance of `Grader`.
  *
  * @since 1.0
  */
trait Grading extends Reporting:
   self: Suite =>

   override val reporter: Grader = Grader()

   /** The reporter, as a grader. */
   def grader: Grader = reporter

   /** Default prettifier, which truncates collections after 32 elements. */
   protected val prettifier: Prettifier = Prettifier.truncateAt(SizeLimit(32))

   given Prettifier = prettifier
