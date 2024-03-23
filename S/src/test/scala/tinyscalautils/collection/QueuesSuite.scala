package tinyscalautils.collection

import org.scalatest.funsuite.AnyFunSuite

import java.util

class QueuesSuite extends AnyFunSuite:
   test("offer"):
      val q = util.ArrayDeque[String]()
      q.offer("X")
      assert(q.peekOption.contains("X"))
      assert(q.pollOption().contains("X"))
      assert(q.pollOption().isEmpty)
