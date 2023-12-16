package tinyscalautils.test.grading

import org.scalatest.{ ConfigMap, Suite }
import tinyscalautils.io.parseURL
import tinyscalautils.text.{ info, plural, timeString }
import tinyscalautils.timing.timeOf

import java.io.IOException
import java.net.URI

/** Grading main application.
  *
  * @note
  *   This application _does_ call `System.exit(0)`.
  *
  * @param suites
  *   a sequence of (weight -> suite) testing suites; tests with names in `failed_tests.txt` are
  *   manually failed.
  */
open class GraderApp(suites: (Int, Suite & Grading)*):
   def main(args: Array[String]): Unit =
      val verbose = args.nonEmpty && args(0) == "-v"
      if verbose then info(newlines = 1)
      var grade   = 0.0
      var weights = 0
      var tests   = 0
      val time = timeOf:
         for (weight, suite) <- suites do
            suite.execute(
              durations = true,
              color = false,
              shortstacks = true,
              configMap = readFailedTests()
            )
            val g = suite.grader.grade * weight
            val n = suite.grader.testCount
            weights += weight
            grade += g
            tests += n
            if verbose then
               println(f"""${suite.suiteName}: $g%.1f / $weight ($n ${plural(n, "test")})""")
      println(s"time: ${timeString(time)}")
      println:
         f"grade: $grade%.0f / $weights" +
            (if verbose then s""" ($tests ${plural(tests, "test")})""" else "")
      System.exit(0) // possible hanging threads; forcing termination

private def readFailedTests(): ConfigMap =
   def parse(line: String) = Some(line.trim).filterNot(str => str.isBlank || str.startsWith("#"))
   try ConfigMap("failed" -> parseURL(failedTestsURL, parse, Set))
   catch case _: IOException => ConfigMap.empty

private val failedTestsURL = URI.create("file:failed_tests.txt").toURL
