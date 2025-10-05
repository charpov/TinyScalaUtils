package tinyscalautils.test.grading

import org.scalactic.{ Prettifier, SizeLimit }
import tinyscalautils.test.text.{ TruncatingPrettifier, noAnalysis }

/** Default prettifier for grading tests.
  *
  * @since 1.2
  */
def gradingPrettifier(prettify: Matchable => Boolean = _ => true): Prettifier =
   val p1 = Prettifier.truncateAt(SizeLimit(32))
   val p2 = TruncatingPrettifier(256)(o => if prettify(o) then p1(o) else o.toString)
   p2.noAnalysis
