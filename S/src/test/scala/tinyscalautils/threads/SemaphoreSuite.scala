package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.timing.timeIt

import java.util.concurrent.Semaphore

class SemaphoreSuite extends AnyFunSuite with Tolerance:
   test("timeout"):
      val sem         = Semaphore(1)
      val (res1, time1) = timeIt(sem.acquire(1, 1.0))
      assert(res1)
      assert(time1 === 0.0 +- 0.1)
      val (res2, time2) = timeIt(sem.acquire(1, 1.0))
      assert(!res2)
      assert(time2 === 1.0 +- 0.1)
