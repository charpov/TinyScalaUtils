package tinyscalautils.test.grading

import org.scalatest.{ ConfigMap, Filter }
import tinyscalautils.io.readAll
import tinyscalautils.text.{ info, plural, timeString }
import tinyscalautils.timing.timeOf

/** Grading main application. It is defined as an open class because subclasses and singleton
  * objects can be run more easily in IntelliJ. Tests with names in a local `failed_tests.txt` file
  * are manually failed. (Lines in this file are interpreted as anchored regular expressions.)
  *
  * @note
  *   This application relies on the default text reporter from Scalatest. Unfortunately, this
  *   reporter uses its own separate thread. As a result, there is an unavoidable race condition
  *   with output from other reporters, if any. If more text output is needed, it should come from
  *   the same default reporter (e.g., using `info`).
  *
  * @note
  *   This application _does_ call `System.exit(0)`.
  *
  * @param suites
  *   an instance of `GradingSuites` taken as a container of testing suites;
  */
open class GraderApp(suites: GradingSuites):
   /** Alternate constructor. It wraps the given suites into a new instance of `GradingSuites`. */
   def this(suites: GradingSuite*) = this(GradingSuites(suites*))

   def main(args: Array[String]): Unit =
      val failedTests = readFailedTests()
      val verbose     = args.nonEmpty && args(0) == "-v"
      if verbose then info(newlines = 1)
      val expectedTests = suites.expectedTestCount(Filter.default)
      println(s"""Starting run for $expectedTests ${plural(expectedTests, "test")}:""")
      val time = timeOf:
         for suite <- suites.nestedSuites do
            suite.execute(
              durations = true,
              color = false,
              shortstacks = true,
              configMap = failedTests
            )
            println:
               val name   = suite.suiteName
               val weight = suite.grader.totalWeight
               val grade  = suite.grader.grade * weight
               val tests  = suite.grader.testCount
               f"""$name: $grade%.1f / $weight%.1f ($tests ${plural(tests, "test")})"""
      println(s"time: ${timeString(time)}")
      println:
         val weight = suites.grader.totalWeight
         val grade  = suites.grader.grade * weight
         val tests  = suites.grader.testCount
         f"""grade: $grade%.0f / $weight%.0f ($tests ${plural(tests, "test")})"""
      System.exit(0) // possible hanging threads; forcing termination
end GraderApp

private def readFailedTests(): ConfigMap =
   import tinyscalautils.io.FileNameIsInput
   def parse(line: String) = Some(line.trim).filterNot(str => str.isBlank || str.startsWith("#"))
   ConfigMap("failed" -> readAll(Set)(failedTestsFile, parse, silent = true))

private val failedTestsFile = "failed_tests.txt"
