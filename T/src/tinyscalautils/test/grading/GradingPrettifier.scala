package tinyscalautils.test.grading

import org.scalactic.{ Prettifier, SizeLimit }
import tinyscalautils.test.text.{ TruncatingPrettifier, noAnalysis }

/** Default prettifier for grading tests. */
@deprecated("use byPass", since = "1.9")
def gradingPrettifier(prettify: Matchable => Boolean = _ => true): Prettifier =
   val p1 = Prettifier.truncateAt(SizeLimit(32))
   val p2 = TruncatingPrettifier(256)(o => if prettify(o) then p1(o) else o.toString)
   p2.noAnalysis

/** Default prettifier for grading tests. */
def gradingPrettifier: Prettifier =
   TruncatingPrettifier(Prettifier.truncateAt(SizeLimit(32)), 256).noAnalysis
