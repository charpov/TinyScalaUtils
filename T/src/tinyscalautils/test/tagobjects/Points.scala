package tinyscalautils.test.tagobjects

import org.scalatest.Tag
import tinyscalautils.assertions.require

import scala.compiletime.error

/** Points tags. These tags are an alternative to using `[...]` strings inside test names.
  *
  * @param points
  *   the desired point value (must be non-negative)
  *
  * @since 1.9
  */
final class Points(points: Double) extends Tag(s"${Points.name}($points)"):
   require(points >= 0.0, s"points must be non-negative, not $points")

/** Companion object. */
object Points:
   /** Common prefix to all `Points` tags: `"tinyscalautils.test.tags.Points"`. */
   val name: String = "tinyscalautils.test.tags.Points"

   extension (n: Int)
      inline def points: Points =
         inline n match
            case 0 => error("0.points not allowed; use 0.point instead")
            case 1 => error("1.points not allowed; use 1.point instead")
            case _ => if n < 0 then error("negative points not allowed") else Points(n)

      inline def pts: Points =
         inline n match
            case 0 => error("0.pts not allowed; use 0.pt instead")
            case 1 => error("1.pts not allowed; use 1.pt instead")
            case _ => if n < 0 then error("negative points not allowed") else Points(n)

   extension (n: 0 | 1)
      def point: Points = Points(n)
      def pt: Points    = Points(n)
