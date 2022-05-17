import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.test.threads.withLocalThreads
import tinyscalautils.lang.unit

import scala.concurrent.Future

class ContextsTests extends AnyFunSuite:
   test("withContext") {
      withLocalThreads(1) {
         Future(unit)
      }
   }
