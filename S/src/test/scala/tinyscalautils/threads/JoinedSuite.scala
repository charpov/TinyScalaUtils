package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.timing.{ sleep, timeIt }
import tinyscalautils.threads.newThread
import tinyscalautils.lang.unit

import java.util.concurrent.CountDownLatch

class JoinedSuite extends AnyFunSuite with Tolerance:

   test("joined, true") {
      val thread = newThread()(unit)
      assert(thread.joined(1.0))
   }

   test("joined, false") {
      val end    = CountDownLatch(1)
      val thread = newThread()(end.await())

      val (terminated, time) = timeIt(thread.joined(1.0))
      end.countDown()
      assert(!terminated)
      assert(time === 1.0 +- 0.1)
      assert(thread.joined(1.0))
   }
