package tinyscalautils.io

import org.scalatest.funsuite.AnyFunSuite
import java.util.MissingResourceException

class ResourcesSuite extends AnyFunSuite:
   test("findResource absolute") {
      val url = this.findResource("/foo.txt")
      assert(url.getPath.endsWith("foo.txt"))
      assertThrows[MissingResourceException](this.findResource("nonexisting"))
   }

   test("findResource relative") {
      val url = this.findResource("rel.txt")
      assert(url.getPath.endsWith("rel.txt"))
      assertThrows[MissingResourceException](this.findResource("nonexisting"))
   }
