package tinyscalautils.io

import org.scalatest.funsuite.AnyFunSuite
import java.util.MissingResourceException

class ResourcesSuite extends AnyFunSuite:

   test("findResource") {
      val url = findResource("foo.txt")
      assert(url.getPath.endsWith("foo.txt"))
      assertThrows[MissingResourceException](findResource("nonexisting"))
   }
