import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite

import java.util.concurrent.atomic.AtomicInteger
import tinyscalautils.test.threads.syncForkJoin
import tinyscalautils.threads.Executors.global
import tinyscalautils.timing.delay

class SyncForkJoinTests extends AnyFunSuite with Tolerance:
   test("syncForkJoin") {
      val sum = AtomicInteger()
      assert(syncForkJoin(1 to 10) { i =>
         delay(1.0)(sum.addAndGet(i))
      } === 1.0 +- 0.1)
      assert(sum.get == 55)
   }
