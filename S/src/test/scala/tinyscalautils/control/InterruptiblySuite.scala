package tinyscalautils.control

import org.scalactic.Tolerance
import org.scalatest.Assertion
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.control.times
import tinyscalautils.lang.{ InterruptibleConstructor, InterruptibleEquality }
import tinyscalautils.threads.shutdownAndWait
import tinyscalautils.timing.sleep

import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy
import java.util.concurrent.{
   ConcurrentLinkedQueue,
   CountDownLatch,
   RejectedExecutionException,
   ThreadFactory
}
import scala.concurrent.Promise
import scala.concurrent.duration.{ Duration, SECONDS }

class InterruptiblySuite extends AnyFunSuite:

   private class C extends InterruptibleConstructor, InterruptibleEquality

   private def runTest[U](code: => U): Assertion =
      val done = CountDownLatch(1000)
      val p    = Promise[Assertion]()
      val task: Runnable = () =>
         p.success {
            assertThrows[InterruptedException] {
               while true do
                  code
                  done.countDown()
            }
         }
      val thread = Thread(task)
      thread.start()
      done.await()
      thread.interrupt()
      thread.join()
      assert(p.future.value.get.isSuccess)

   test("interruptibleConstructor") {
      runTest(C())
   }

   test("interruptibleEquality") {
      val c = C()
      runTest(c == c)
      runTest(c.##)
   }

   test("interruptibly") {
      runTest(interruptibly(()))
   }
