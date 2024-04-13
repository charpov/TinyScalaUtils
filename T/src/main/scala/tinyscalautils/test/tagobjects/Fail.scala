package tinyscalautils.test.tagobjects

import org.scalatest.Tag
import tinyscalautils.assertions.{checkNonNull, require}

import scala.util.matching.Regex

/** A tag to fail a test. This makes it easier to manually fail tests in grading runs.
  *
  * @see
  *   [[tinyscalautils.test.grading.GradingRun]]
  *
  * @since 1.1
  */
class Fail private (name: String) extends Tag(name)

/** A failed tag with no message
  *
  * @since 1.1
  */
object Fail extends Fail("tinyscalautils.test.tags.Fail"):
   private[test] val regex: Regex = raw"${Fail.name}(?:\((.*)\))?".r

   /** A new failed tag with the given message. */
   def apply(message: String): Fail =
      require(checkNonNull(message).nonEmpty, "message cannot be null or empty")
      new Fail(s"${Fail.name}($message)")
