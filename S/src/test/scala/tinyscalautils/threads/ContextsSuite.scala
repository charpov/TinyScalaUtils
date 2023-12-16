package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.control.times
import tinyscalautils.threads.shutdownAndWait
import tinyscalautils.timing.{ delay, sleep, timeIt, timeOf }

import java.util.concurrent.CyclicBarrier
import scala.concurrent.{ ExecutionContext, Future }

class ContextsSuite extends AnyFunSuite with Tolerance:
   for (flag <- Seq(false, true)) do
      test(s"withThreadPoolAndWait (shutdown = $flag)"):
         val exec = Executors.newUnlimitedThreadPool()
         val time = withThreadPoolAndWait(exec, shutdown = flag)(Future(timeOf(sleep(1.0))))
         assert(exec.isShutdown == flag)
         assert(exec.shutdownAndWait(0.1))
         assert(time === 1.0 +- 0.01)

      test(s"withThreadsAndWait (awaitTermination = $flag)"):
         val (time1, time2) = timeIt:
            withThreadsAndWait(4, awaitTermination = flag):
               Execute(sleep(2.0))
               Future(timeOf(sleep(1.0)))
         val expectedTime = if flag then 2.0 else 1.0
         assert(time1 === 1.0 +- 0.1)
         assert(time2 === expectedTime +- 0.1)

      test(s"withUnlimitedThreadsAndWait (awaitTermination = $flag)"):
         val (time1, time2) = timeIt:
            withUnlimitedThreadsAndWait(awaitTermination = flag):
               Execute(sleep(2.0))
               Future(timeOf(sleep(1.0)))
         val expectedTime = if flag then 2.0 else 1.0
         assert(time1 === 1.0 +- 0.1)
         assert(time2 === expectedTime +- 0.1)

      test(s"withThreadPoolAndWait, exception (shutdown = $flag)"):
         object Ex extends Exception
         val exec = Executors.newUnlimitedThreadPool()
         val ex = intercept[Exception]:
            withThreadPoolAndWait(exec, shutdown = flag)(Future(throw Ex))
         assert(exec.isShutdown == flag)
         assert(exec.shutdownAndWait(1.0))
         assert(ex eq Ex)

      test(s"withThreads (awaitTermination = $flag)"):
         val time = timeOf:
            withThreads(4, awaitTermination = flag):
               10 times Execute(sleep(1.0))
         val expectedTime = if flag then 3.0 else 0.0
         assert(time === expectedTime +- 0.1)

      test(s"withUnlimitedThreads (awaitTermination = $flag)"):
         val n       = 100
         val barrier = CyclicBarrier(n)
         val time = timeOf:
            withUnlimitedThreads(awaitTermination = flag):
               n times Execute(barrier.await())
               Execute(sleep(1.0))
         val expectedTime = if flag then 1.0 else 0.0
         assert(time === expectedTime +- 0.1)
   end for

   test("withThreadPoolAndWait, no shutdown"):
      inline val flag = false
      assertResult(42):
         withThreadPoolAndWait(ExecutionContext.global, shutdown = flag):
            Future(delay(1.0)(42))

/*
   test("simplified methods"):
      assert(withUnlimitedThreadsAndWait(Future(true)))
 */
