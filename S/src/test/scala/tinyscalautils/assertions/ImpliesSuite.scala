package tinyscalautils.assertions

import org.scalatest.funsuite.AnyFunSuite

class ImpliesSuite extends AnyFunSuite:

   private class Caller[A]:
      var called = false

      def call(value: A): A =
         called = true
         value

      def reset(): Unit = called = false

   test("implies") {
      assert(false implies false)
      assert(false implies true)
      assert(true implies true)
      assert(!(true implies false))
   }

   test("implies, lazy") {
      val caller = Caller[Boolean]()
      assert(false implies caller.call(false))
      assert(!caller.called)
      caller.reset()
      assert(false implies caller.call(true))
      assert(!caller.called)
      caller.reset()
      assert(true implies caller.call(true))
      assert(caller.called)
      caller.reset()
      assert(!(true implies caller.call(false)))
      assert(caller.called)
   }

   test("==>") {
      assert(false ==> false)
      assert(false ==> true)
      assert(true ==> true)
      assert(!(true ==> false))
   }

   test("==>, lazy") {
      val caller = Caller[Boolean]()
      assert(false ==> caller.call(false))
      assert(!caller.called)
      caller.reset()
      assert(false ==> caller.call(true))
      assert(!caller.called)
      caller.reset()
      assert(true ==> caller.call(true))
      assert(caller.called)
      caller.reset()
      assert(!(true ==> caller.call(false)))
      assert(caller.called)
   }
