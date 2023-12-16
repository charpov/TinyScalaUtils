package tinyscalautils.test.assertions

import org.scalactic.Prettifier
import org.scalatest.Assertion
import org.scalatest.Assertions.assertResult

/** Asserts that an expression has a value. This is `assertResult` in reverse order.
  *
  * @since 1.1
  */
def assertExpr[A, B](actual: A)(expected: B)(using Prettifier, CanEqual[A, B]): Assertion =
   assertResult(expected)(actual)

/** Asserts that an expression has a value. This is `assertResult` in reverse order.
  *
  * @since 1.1
  */
def assertExpr[A, B](actual: A, clue: Any)(
    expected: B
)(using Prettifier, CanEqual[A, B]): Assertion = assertResult(expected, clue)(actual)
