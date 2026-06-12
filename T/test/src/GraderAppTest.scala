import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.lang.unit
import tinyscalautils.test.grading.{ GraderApp, Grading, GradingSuites }

// run inside "T" to fail tests

private class Tests1 extends AnyFunSuite with Grading(20):
   override def suiteName = "Tests1"
   test("pass 1 [2pts]")(unit)
   test("fail")(unit)
   test("pass 2 [2pts]")(unit)

private class Tests2 extends AnyFunSuite with Grading:
   override def suiteName = "Tests2"
   test("fail 1 [4pts]")(unit)
   test("pass [5pts]")(unit)
   test("fail 2")(unit)

private class Combined extends GradingSuites(10)(Tests1(), Tests2()):
   override def suiteName = "Combined"

private class AllTests extends GradingSuites(Tests1(), Tests2(), Combined())

object GraderAppTest extends GraderApp(AllTests())
