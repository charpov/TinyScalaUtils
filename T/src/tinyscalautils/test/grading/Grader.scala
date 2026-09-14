package tinyscalautils.test.grading

/** A grade reporter.
  *
  * @see
  *   [[package.Grading]]
  */
trait Grader:
   /** The grade.  This is a number between 0.0 and 1.0. */
   def grade: Double

   /** Total weight of the graded suite. */
   def totalWeight: Double

   /** Total number of tests that were run, not including nested suites. */
   def testCount: Int

   /** Total number of failed tests, not including nested suites. */
   def failureCount: Int
end Grader
