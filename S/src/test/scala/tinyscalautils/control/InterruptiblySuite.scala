package tinyscalautils.control

import org.scalatest.Assertion
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.lang.{InterruptibleConstructor, InterruptibleEquality, unit}
import tinyscalautils.threads.{joined, newThread}

import java.util.concurrent.CountDownLatch
import scala.concurrent.Promise

class InterruptiblySuite extends AnyFunSuite:
   private class C extends InterruptibleConstructor, InterruptibleEquality

   private def runTest[U](code: => U): Assertion =
      val done = CountDownLatch(1000)
      val p    = Promise[Assertion]()
      def task =
         p.success:
            assertThrows[InterruptedException]:
               while true do
                  code
                  done.countDown()
      val thread = newThread(task)
      done.await()
      thread.interrupt()
      assert(thread.joined(1.0))
      assert(p.future.value.get.isSuccess)

   test("interruptibleConstructor")(runTest(C()))

   test("interruptibleEquality"):
      val c = C()
      runTest(c == c)
      runTest(c.##)

   test("interruptibly")(runTest(interruptibly(unit)))
