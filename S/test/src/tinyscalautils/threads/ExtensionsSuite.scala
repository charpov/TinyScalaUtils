package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.timing.timeIt

import java.util.concurrent.Semaphore
import java.util.concurrent.ArrayBlockingQueue

class ExtensionsSuite extends AnyFunSuite with Tolerance:
   test("semaphore timeout"):
      val sem           = Semaphore(1)
      val (res1, time1) = timeIt(sem.acquire(1, 1.0))
      assert(res1)
      assert(time1 === 0.0 +- 0.1)
      val (res2, time2) = timeIt(sem.acquire(1, 1.0))
      assert(!res2)
      assert(time2 === 1.0 +- 0.1)

   test("queue timeout"):
      val q             = ArrayBlockingQueue[String](1)
      val (res1, time1) = timeIt(q.offer("X", seconds = 1.0))
      assert(res1)
      assert(time1 === 0.0 +- 0.1)
      val (res2, time2) = timeIt(q.offer("X", seconds = 1.0))
      assert(!res2)
      assert(time2 === 1.0 +- 0.1)
      val (res3, time3) = timeIt(q.poll(seconds = 1.0))
      assert(res3 == "X")
      assert(time3 === 0.0 +- 0.1)
      val (res4, time4) = timeIt(q.poll(seconds = 1.0))
      assert(res4 == null)
      assert(time4 === 1.0 +- 0.1)
