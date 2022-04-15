package tinyscalautils.lang

import org.scalatest.funsuite.AnyFunSuite

class UnitSuite extends AnyFunSuite:

   test("unit") {
      def f[A](p: (A, A)) = p(0)
      val u1              = f(((), ()))
      val u2              = f((unit, unit))
      assert(u1 === u2)
   }
