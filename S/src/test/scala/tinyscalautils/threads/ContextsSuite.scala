package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.control.times
import tinyscalautils.threads.shutdownAndWait
import tinyscalautils.timing.{ delay, sleep, timeIt, timeOf }

import java.util.concurrent.CyclicBarrier
import scala.concurrent.{ ExecutionContext, Future }

class ContextsSuite extends AnyFunSuite with Tolerance:
   for flag <- Seq(false, true) do
      test(s"withThreads, wait (shutdown = $flag)"):
         val exec = Executors.newUnlimitedThreadPool()
         val time = withThreads(exec, shutdown = flag)(Future(timeOf(sleep(1.0))))
         assert(exec.isShutdown == flag)
         assert(exec.shutdownAndWait(0.1))
         assert(time === 1.0 +- 0.01)

      test(s"withThreads, wait (awaitTermination = $flag)"):
         val (time1, time2) = timeIt:
            withThreads(4, awaitTermination = flag):
               Execute(sleep(2.0))
               Future(timeOf(sleep(1.0)))
         val expectedTime = if flag then 2.0 else 1.0
         assert(time1 === 1.0 +- 0.1)
         assert(time2 === expectedTime +- 0.1)

      test(s"withThreads, unlimited, wait (awaitTermination = $flag)"):
         val (time1, time2) = timeIt:
            withThreads(awaitTermination = flag):
               Execute(sleep(2.0))
               Future(timeOf(sleep(1.0)))
         val expectedTime = if flag then 2.0 else 1.0
         assert(time1 === 1.0 +- 0.1)
         assert(time2 === expectedTime +- 0.1)

      test(s"withThreads, wait, exception (shutdown = $flag)"):
         val Ex   = Exception()
         val exec = Executors.newUnlimitedThreadPool()
         val ex = intercept[Exception]:
            withThreads(exec, shutdown = flag)(Future(throw Ex))
         assert(exec.isShutdown == flag)
         assert(exec.shutdownAndWait(1.0))
         assert(ex eq Ex)

      test(s"withThreads (awaitTermination = $flag)"):
         val time = timeOf:
            withThreads(4, awaitTermination = flag):
               10 times Execute(sleep(1.0))
         val expectedTime = if flag then 3.0 else 0.0
         assert(time === expectedTime +- 0.1)

      test(s"withThreads, unlimited (awaitTermination = $flag)"):
         val n       = 100
         val barrier = CyclicBarrier(n)
         val time = timeOf:
            withThreads(awaitTermination = flag):
               n times Execute(barrier.await())
               Execute(sleep(1.0))
         val expectedTime = if flag then 1.0 else 0.0
         assert(time === expectedTime +- 0.1)
   end for

   test("withThreads, wait, no shutdown"):
      inline val flag = false
      assertResult(42):
         withThreads(ExecutionContext.global, shutdown = flag):
            Future(delay(1.0)(42))

   test("simplified method"):
      assert(withThreads()(Future(true)))

   test("capturing a future"):
      val f = withThreads():
         Future.successful:
            Future(delay(1.0)("X"))
      assert(!f.isCompleted)
      val (v, time) = timeIt(withThreads()(f))
      assert(v == "X")
      assert(time === 1.0 +- 0.1)
