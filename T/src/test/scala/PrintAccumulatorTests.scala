import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.text.{PrintingMode, print, printf, println, threadTimeDemoMode}
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

   test("with mode") {
      given lines: PrintAccumulator = PrintAccumulator(threadTimeDemoMode)
      println("A")
      val line1 = lines.resetLines().head
      assert(line1.contains(" at XX:XX:") && line1.contains(": A"))
      printf("value=%d", 42)
      val line2 = lines.resetLines().head
      assert(line2.contains(" at XX:XX:") && line2.contains(": value=42"))
   }
