import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.control.times
import tinyscalautils.test.threads.withMarkedThreads
import tinyscalautils.threads.*

import java.util.concurrent.{ CyclicBarrier, Semaphore }
import scala.concurrent.Future
class WithMarkedThreadsSuite extends AnyFunSuite:
   private def waitForInterrupt() =
      try Semaphore(0).acquire()
      catch case _: InterruptedException => ()

   test("interrupting 1"):
      val barrier = CyclicBarrier(5)
      val thread = newThread:
         try
            withMarkedThreads(4):
               4 times:
                  Run:
                     barrier.await()
                     waitForInterrupt()
                     barrier.await()
         catch case _: InterruptedException => ()
      barrier.await()
      thread.interrupt()
      assert(thread.joined(0.1))
      barrier.await(0.1)

   test("interrupting 2"):
      val barrier = CyclicBarrier(5)
      val thread = newThread:
         try
            withMarkedThreads(4):
               Future.sequence:
                  Seq.fill(4):
                     Future:
                        barrier.await()
                        waitForInterrupt()
                        barrier.await()
         catch case _: InterruptedException => ()
      barrier.await()
      thread.interrupt()
      assert(thread.joined(0.1))
      barrier.await(0.1)
