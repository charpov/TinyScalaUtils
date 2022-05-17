package tinyscalautils.test.threads

import org.scalatest.Assertion
//import scala.language.implicitConversions
import scala.concurrent.Future

/** An implicit conversion from `Assertion` to `Future[Assertion]`. This is similar to what is in
  * the `async` style of ScalaTest.
  *
  * @since 1.0
  */
given AssertionFutureConversion: Conversion[Assertion, Future[Assertion]] = Future.successful(_)
