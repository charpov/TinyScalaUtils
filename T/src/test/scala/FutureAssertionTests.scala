import org.scalatest.Assertion
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.test.threads.AssertionFutureConversion

import scala.concurrent.Future
import scala.language.implicitConversions

class FutureAssertionTests extends AnyFunSuite:
   test("implicit conversion") {
      def use(f: Future[Assertion]) = f.isCompleted
      assert(use(assert(true)))
   }
