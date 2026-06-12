package tinyscalautils.test.grading

import org.scalatest.Suite

/** A trait for test suites that have a grader.
  *
  * @since 1.3
  */
trait GradingSuite extends Suite:
   /** The grader. */
   def grader: Grader
