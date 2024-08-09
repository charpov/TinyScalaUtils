package tinyscalautils.test.grading

import org.scalactic.{ Prettifier, SizeLimit }
import tinyscalautils.test.text.{ TruncatingPrettifier, noAnalysis }

/** Default prettifier for grading tests.
  *
  * @since 1.2
  */
def gradingPrettifier(bypass: Matchable => Boolean = _ => false): Prettifier =
   val p1 = Prettifier.truncateAt(SizeLimit(32))
   val p2 = TruncatingPrettifier(256)(o => if bypass(o) then o.toString else p1(o))
   p2.noAnalysis
