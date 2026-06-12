package tinyscalautils.assertions

import org.scalatest.funsuite.AnyFunSuite

class ImpliesSuite extends AnyFunSuite:
   test("implies"):
      assert(false implies false)
      assert(false implies true)
      assert(true implies true)
      assert(!(true implies false))

   test("implies, lazy"):
      assert(false implies (throw AssertionError()))
