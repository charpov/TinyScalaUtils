package tinyscalautils.timing

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.withUnlimitedThreadsAndWait

import scala.concurrent.Future

class TimingSuite extends AnyFunSuite with Tolerance:

   private val obj = Object()

   test("timeOf") {
      val time = timeOf {
         delay(1.5)(obj)
      }
      assert(time === 1.5 +- 0.1)
   }

   test("timeIt") {
      val (value, time) = timeIt {
         delay(1.5)(obj)
      }
      assert(value eq obj)
      assert(time === 1.5 +- 0.1)
   }

   test("zipWithDuration") {
      withUnlimitedThreadsAndWait() {
         val future = Future(delay(1.5)(obj))
         val (f, t) = timeIt(future.zipWithDuration)
         assert(t === 0.0 +- 0.1)
         f.map { (value, time) =>
            assert(value eq obj)
            assert(time === 1.5 +- 0.1)
         }
      }
   }
