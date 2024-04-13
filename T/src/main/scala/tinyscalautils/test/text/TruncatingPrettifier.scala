package tinyscalautils.test.text

import org.scalactic.{ Prettifier, PrettyPair }
import tinyscalautils.text.short

/** A truncating prettifier. It can be used to guarantee that failed tests do not produce humongous
  * outputs.
  *
  * @constructor
  * @param prettifier
  *   the underlying prettifier.
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
     */
   def this(limit: Int)(using DummyImplicit)(prettifier: Prettifier) = this(prettifier, limit)

   private def s(str: String) = str.short(limit)

   def apply(o: Any): String = s(prettifier(o))

   override def apply(left: Any, right: Any): PrettyPair =
      val PrettyPair(l, r, a) = super.apply(left, right)
      PrettyPair(s(l), s(r), a.map(s))
end TruncatingPrettifier

extension (prettifier: Prettifier)
   /** Deactivates "analysis", which runs very slow on large data structures. */
   def noAnalysis: Prettifier = new Prettifier:
      def apply(o: Any): String = prettifier(o)

      override def apply(left: Any, right: Any): PrettyPair =
         PrettyPair(prettifier(left), prettifier(right), None)
   end noAnalysis
