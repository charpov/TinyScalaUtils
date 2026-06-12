package tinyscalautils.test.assertions

import org.scalactic.{ Prettifier, source }
import org.scalatest.Assertions.assert
import org.scalatest.Assertion

/** Checks that two multisets are equal.
  *
  * @since 1.0
  */
def assertSameElements[A](values: Seq[A], expected: Seq[A])(
    using Prettifier,
    source.Position
): Assertion =
   assert(values.lengthCompare(expected) == 0, ": not the same number of elements")
   assert(values.diff(expected).isEmpty, ": unexpected elements")
