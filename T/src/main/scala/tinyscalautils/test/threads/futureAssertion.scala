package tinyscalautils.test.threads

import org.scalatest.Assertion
import scala.concurrent.Future

/** An implicit conversion from `Assertion` to `Future[Assertion]`. This is similar to what is in
  * the `async` style of ScalaTest.
  *
  * @since 1.0
  */
@deprecated("not needed with new implementation of withThreads", since = "1.7")
given AssertionFutureConversion: Conversion[Assertion, Future[Assertion]] = Future.successful(_)
