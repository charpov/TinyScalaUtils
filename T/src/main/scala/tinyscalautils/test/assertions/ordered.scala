package tinyscalautils.test.assertions

import org.scalactic.{ Prettifier, source }
import org.scalatest.Assertions.assert
import org.scalatest.{ Succeeded, Assertion }
import scala.annotation.tailrec
import scala.math.Ordering.Implicits.infixOrderingOps

/** Checks that a list is in non-decreasing order.
  *
  * @since 1.0
  */
@tailrec
def assertInOrder[A : Ordering](list: List[A])(using Prettifier, source.Position): Assertion =
   list match
      case first :: (more @ second :: _) =>
         assert(first <= second, ": out of order")
         assertInOrder(more)
      case _ => Succeeded

/** Checks that a sequence is in non-decreasing order.
  *
  * @since 1.0
  */
def assertInOrder[A : Ordering](seq: IndexedSeq[A])(using Prettifier, source.Position): Assertion =
   for index <- 1 until seq.length do assert(seq(index - 1) <= seq(index), ": out of order")
   Succeeded
