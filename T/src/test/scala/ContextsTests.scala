import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.withThreadsAndWait
import tinyscalautils.lang.unit

import scala.concurrent.Future

class ContextsTests extends AnyFunSuite:
   test("withThreadsAndWait") {
      withThreadsAndWait(1) {
         Future(unit)
      }
   }
