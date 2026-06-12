package tinyscalautils.control

import org.scalatest.funsuite.AnyFunSuite

class BeforeSuite extends AnyFunSuite:
   test("int"):
      var c = 0
      assert((c before (c += 1)) == 0)
      assert(c == 1)

   test("string"):
      var c = 0
      assert((c.toString before (c += 1)) == "0")
      assert(c == 1)

   test("throw"):
      val e = intercept[Exception]((throw Exception("A")) before (throw Exception("B")))
      assert(e.getMessage == "A")
