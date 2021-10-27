package tinyscalautils.test.grading

import org.scalatest.Suite
import tinyscalautils.timing.timeOf
import tinyscalautils.test.mixins.Grading

/** Grading main application. Note that this application ''does'' call `System.exit(0)`.
  *
  * @param suites
  *   a sequence of (weight -> suite) testing suites
  */
class GraderApp(suites: (Int, Suite & Grading)*):

   def main(args: Array[String]): Unit =
      val verbose = args.nonEmpty && args(0) == "-v"
      var grade   = 0.0
      var weights = 0
      val time = timeOf {
         for ((weight, suite) <- suites) do
            suite.execute(durations = true, color = false)
            val g = suite.grader.grade * weight
            weights += weight
            grade += g
            if verbose then println(f"${suite.suiteName}: $g%.1f / $weight")
      }
      println(f"time: $time%.1f seconds")
      println(f"grade: $grade%.0f / $weights")
      System.exit(0) // possible hanging threads; forcing termination
