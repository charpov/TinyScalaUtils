package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.lang.StackOverflowException
import tinyscalautils.threads.Executors.global
import tinyscalautils.timing.{ sleep, timeOf }

class RunAsyncSuite extends AnyFunSuite with Tolerance:
   test("runAsync 1"):
      assertResult("X"):
         runAsync:
            sleep(0.5)
            "X"

   test("runAsync 2"):
      val runner = Thread.currentThread
      timeoutTimer.schedule(1.0)(runner.interrupt())
      val time = timeOf:
         assertThrows[InterruptedException]:
            withUnlimitedThreads(awaitTermination = true):
               runAsync:
                  sleep(10)
                  assert(Thread.currentThread().isInterrupted)
                  sleep(10)
      assert(time === 1.0 +- 0.1)

   test("no stack overflow"):
      assertThrows[StackOverflowException]:
         withUnlimitedThreads()(runAsync(throw StackOverflowError()))
