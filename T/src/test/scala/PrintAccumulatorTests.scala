import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.text.{ PrintingMode, println, print, printf }
import tinyscalautils.test.text.PrintAccumulator
import tinyscalautils.test.threads.syncForkJoin
import tinyscalautils.threads.Executors.global
import tinyscalautils.threads.availableProcessors

class PrintAccumulatorTests extends AnyFunSuite:
   test("single thread") {
      given lines: PrintAccumulator = PrintAccumulator()
      println("A")
      print("B")
      assert(lines.resetLines() == Seq("A\n", "B"))
      printf("%d", 42)
      assert(lines.resetLines() == Seq("42"))
   }

   test("multi threads") {
      given lines: PrintAccumulator = PrintAccumulator()
      for n <- 1 to availableProcessors * 2 do
         val strings = Set.tabulate(n)(_.toString)
         syncForkJoin(strings)(print)
         assert(lines.resetLines().toSet == strings)
   }
