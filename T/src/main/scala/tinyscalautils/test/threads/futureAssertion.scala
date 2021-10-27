package tinyscalautils.test.threads

import org.scalatest.Assertion

import scala.concurrent.Future
import scala.language.implicitConversions

/** An implicit conversion from `Assertion` to `Future[Assertion]`. This is similar to what is in
  * the `async` style of ScalaTest.
  *
  * @since 1.0
  */
implicit def futureAssertions(assertion: Assertion): Future[Assertion] =
   Future.successful(assertion)
