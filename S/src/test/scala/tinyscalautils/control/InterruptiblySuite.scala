package tinyscalautils.control

import org.scalatest.Assertion
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.lang.{ InterruptibleConstructor, InterruptibleEquality, unit }
import tinyscalautils.threads.{ joined, newThread }

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import scala.Function.untupled
import scala.concurrent.Promise

class InterruptiblySuite extends AnyFunSuite:
   private class C extends InterruptibleConstructor, InterruptibleEquality

   private def runTest1(code: => Any): Assertion =
      val done = CountDownLatch(1000)
      val p    = Promise[Assertion]()
      def task =
         p.success:
            assertThrows[InterruptedException]:
               while true do
                  code
                  done.countDown()
      val thread = newThread(task)
      done.await()
      thread.interrupt()
      assert(thread.joined(1.0))
      assert(p.future.value.get.isSuccess)

   private def runTest2(code: => Any, run: AtomicBoolean) =
      try
         Thread.currentThread.interrupt()
         assertThrows[InterruptedException](code)
         assert(!run.get())
      finally assert(!Thread.interrupted())

   private def f1(str: String)         = str.length
   private def f2(str: String, x: Int) = str.length + x

   test("interruptibleConstructor"):
      runTest1(C())

   test("interruptibleEquality"):
      val c = C()
      runTest1(c == c)
      runTest1(c.##)

   test("interruptibly code never"):
      var called = false
      Thread.currentThread.interrupt()
      assertThrows[InterruptedException](interruptibly { called = true })
      assert(!called)

   test("interruptibly code loop"):
      runTest1(interruptibly(unit))

   test("Function1.interruptibly never"):
      val a             = AtomicBoolean()
      def f(b: Boolean) = a.set(b)
      val g             = f.interruptibly
      runTest2(g(true), a)

   test("Function1.interruptibly loop"):
      runTest1(f1.interruptibly)
      val g = f1.interruptibly
      runTest1(g("X"))

   test("Function2.interruptibly never"):
      val a                 = AtomicBoolean()
      def f(x: Int, y: Int) = a.set(x == y)
      val g                 = f.interruptibly
      runTest2(g(0, 0), a)

   test("Function2.interruptibly loop"):
      runTest1(f2.interruptibly)
      val g = f2.interruptibly
      runTest1(g("X", 1))

   test("higher-order interruptibly 1"):
      var n         = 0
      def p(x: Int) = { if x == 2 then Thread.currentThread.interrupt() else n = x; true }
      try
         assertThrows[InterruptedException](List(1, 2, 3).forall.interruptibly(p))
         assert(n == 1)
      finally assert(!Thread.interrupted())

   test("higher-order interruptibly 2"):
      var n                    = 0
      def f(a: String, x: Int) = { if x == 2 then Thread.currentThread.interrupt() else n = x; a }
      try
         assertThrows[InterruptedException](List(1, 2, 3).foldLeft("").interruptibly(f))
         assert(n == 1)
      finally assert(!Thread.interrupted())

   test("higher-order interruptibly 3"):
      var n         = 0
      def f(x: Int) = if x == 2 then Thread.currentThread.interrupt() else n = x
      try
         assertThrows[InterruptedException](List(1, 2, 3).foreach.interruptibly(f))
         assert(n == 1)
      finally assert(!Thread.interrupted())

   test("higher-order interruptibly 4"):
      try
         Thread.currentThread.interrupt()
         assertThrows[InterruptedException](Nil.foreach.interruptibly(_ => unit))
         Thread.currentThread.interrupt()
         assertThrows[InterruptedException](Nil.forall.interruptibly(_ => true))
         Thread.currentThread.interrupt()
         assertThrows[InterruptedException](Nil.foldLeft(0).interruptibly((_, _) => 0))
      finally assert(!Thread.interrupted())

   test("higher-order interruptibly 5"):
      val list                      = List(1, 2, 3)
      def f(x: Int, y: Int, z: Int) = { Thread.currentThread.interrupt(); x + y + z }
      val h                         = (list lazyZip list lazyZip list).map
      try
         assert(h.interruptibly(f) == List(3, 6, 9))
         assert(Thread.interrupted())
         assertThrows[InterruptedException](h(untupled(f.tupled.interruptibly)))
      finally assert(!Thread.interrupted())
