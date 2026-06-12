package tinyscalautils.test.text

import org.scalactic.{ Prettifier, PrettyPair }
import tinyscalautils.text.short

import scala.compiletime.asMatchable
import scala.reflect.TypeTest

/** A truncating prettifier. It can be used to guarantee that failed tests do not produce humongous
  * outputs.
  *
  * @constructor
  * @param prettifier
  *   the underlying prettifier.
  *
  * @param limit
  *   the maximum length of individual strings; must be at least 3.
  */
class TruncatingPrettifier(prettifier: Prettifier, limit: Int) extends Prettifier:
   require(limit >= 3, s"limit $limit must be at least 3")

   /** Uses implicit prettifier as underlying. */
   def this(limit: Int)(using prettifier: Prettifier) = this(prettifier, limit)

   /** Specifies underlying prettifier in curried form, e.g.:
     * {{{
     *   TruncatingPrettifier(256): o =>
     *      ...
     * }}}
     * or
     * {{{
     *   TruncatingPrettifier(256):
     *      case ... => ...
     *      case ... => ...
     * }}}
     */
   def this(limit: Int)(prettifier: Matchable => String) =
      this(o => prettifier(o.asMatchable), limit)

   private def s(str: String) = str.short(limit)

   def apply(o: Any): String = s(prettifier(o))

   override def apply(left: Any, right: Any): PrettyPair =
      val PrettyPair(l, r, a) = super.apply(left, right)
      PrettyPair(s(l), s(r), a.map(s))
end TruncatingPrettifier

private class NoAnalysis(val prettifier: Prettifier) extends Prettifier:
   def apply(o: Any): String = prettifier(o)

   override def apply(left: Any, right: Any): PrettyPair =
      PrettyPair(prettifier(left), prettifier(right), None)
end NoAnalysis

private def fromNoAnalysis(prettifier: Prettifier) =
   prettifier match
      case noAnalysis: NoAnalysis => noAnalysis.prettifier
      case _                      => prettifier

extension (prettifier: Prettifier)
   /** Deactivates "analysis", which runs very slowly on large data structures. */
   def noAnalysis: Prettifier = NoAnalysis(prettifier)

   /** Bypasses prettifying of objects that satisfy the condition. */
   def bypass(skip: Matchable => Boolean): Prettifier =
      val basePrettifier = fromNoAnalysis(prettifier)
      val byPassed = Prettifier(o => if skip(o.asMatchable) then o.toString else basePrettifier(o))
      if basePrettifier eq prettifier then byPassed else byPassed.noAnalysis

   /** Bypasses prettifying of objects of the given type. */
   def byPass[A](using TypeTest[Matchable, A]): Prettifier =
      val basePrettifier = fromNoAnalysis(prettifier)
      val byPassed       = Prettifier: o =>
         o.asMatchable match
            case _: A => o.toString
            case _    => basePrettifier(o)
      if basePrettifier eq prettifier then byPassed else byPassed.noAnalysis
end extension
