package tinyscalautils.test.tagobjects

import org.scalatest.Tag
import tinyscalautils.assertions.require

import scala.util.matching.Regex

/** Timeout tags. These tags can be used within `DualTimeLimits` when `Fast` and `Slow` are not
  * enough.
  *
  * @param seconds
  *   the desired timeout, in seconds (must be positive)
  *
  * @see
  *   [[tinyscalautils.test.mixins.DualTimeLimits]]
  *
  * @since 1.1
  */
final class Timeout(seconds: Double) extends Tag(s"${Timeout.name}($seconds)"):
   require(seconds > 0.0, "timeout must be positive")

/** Companion object. */
object Timeout:
   /** Common prefix to all `Timeout` tags: `"tinyscalautils.test.tags.Timeout"`. */
   val name: String               = "tinyscalautils.test.tags.Timeout"
   private[test] val regex: Regex = raw"$name\((.*)\)".r
