package tinyscalautils

import org.scalatest.funsuite.AnyFunSuite

// outside control package to test givens

class InterruptiblySuite extends AnyFunSuite:
   test("plain"):
      import tinyscalautils.control.interruptibly
      assert(interruptibly("X") == "X")
      Thread.currentThread.interrupt()
      assertThrows[InterruptedException](interruptibly("X"))
      assert(!Thread.currentThread.isInterrupted)

   test("functions"):
      import tinyscalautils.control.InterruptiblyExtensions
      val list = List(1, 2, 3)
      assert(list.map.interruptibly(_ + 1) == List(2, 3, 4))
      Thread.currentThread.interrupt()
      assertThrows[InterruptedException](list.map.interruptibly(_ + 1))
      assert(!Thread.currentThread.isInterrupted)

   test("ambiguous 1"):
      import tinyscalautils.control.interruptibly
      val set = Set(1, 2, 3)
      assert(interruptibly(set) == Set(1, 2, 3))

   test("ambiguous 2"):
      import tinyscalautils.control.InterruptiblyExtensions
      val set = Set(1, 2, 3)
      assert(set.interruptibly(2))

   test("ambiguous 3"):
      import tinyscalautils.control.{ InterruptiblyExtensions, interruptibly }
      val set = Set(1, 2, 3)
      assert(interruptibly(set) == Set(1, 2, 3))
      assert(set.interruptibly(2))
