package tinyscalautils.test.grading

/** A class to combine multiple grading suites. This is a grade-aware replacement for
  * `org.scalatest.Suites`.
  *
  * @since 1.3
  */
open class GradingSuites private (
    defaultTotalWeight: Double,
    override val nestedSuites: IndexedSeq[GradingSuite]
) extends GradingSuite:

   /** Creates a combined suite.Its weight is the sum of the weights of the nested suites. */
   def this(weight: Double)(suites: GradingSuite*) = this(weight, suites.toIndexedSeq)

   /** Creates a combined suite. Its weight is given and must be positive. */
   def this(suites: GradingSuite*) = this(0.0, suites.toIndexedSeq)

   val grader: Grader = Graders()

   private class Graders extends Grader:
      private def graders = nestedSuites.map(_.grader)

      private def gradeSum = graders.map(g => g.grade * g.totalWeight).sum

      private def sumWeight: Double = graders.map(_.totalWeight).sum

      def totalWeight: Double = if defaultTotalWeight > 0.0 then defaultTotalWeight else sumWeight

      def testCount: Int = graders.map(_.testCount).sum

      def grade: Double = gradeSum / sumWeight
end GradingSuites
