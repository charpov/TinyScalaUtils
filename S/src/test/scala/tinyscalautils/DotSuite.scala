package tinyscalautils

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.text.{ dot, printout, star }
import tinyscalautils.util.FastRandom

// must reside outside text package to use default given arguments

class DotSuite extends AnyFunSuite:
   private val data = FastRandom.nextString(10)

   test("standard"):
      import tinyscalautils.text.standardMode
      val out = printout:
         assert(dot(data) == data)
      assert(out == ".")

   test("silent"):
      import tinyscalautils.text.silentMode
      val out = printout:
         assert(dot(data) == data)
      assert(out.isEmpty)

   test("default dot"):
      val out = printout:
         assert(dot(data) == data)
      assert(out == ".")

   test("default star"):
      val out = printout:
         assert(star(data) == data)
      assert(out == "*")

   test("forbidden modes"):
      assertTypeError("import tinyscalautils.text.threadMode\ndot(data)")
      assertTypeError("import tinyscalautils.text.threadMode\nstr(data)")
