package tinyscalautils.io

import org.scalatest.funsuite.AnyFunSuite
import java.util.MissingResourceException

class ResourcesSuite extends AnyFunSuite:

   given Class[ResourcesSuite] = classOf[ResourcesSuite]

   test("findResource absolute") {
      val url = getClass.findResource("/foo.txt")
      assert(url.getPath.endsWith("foo.txt"))
      assertThrows[MissingResourceException](getClass.findResource("nonexisting"))
   }

   test("findResource relative") {
      val url = getClass.findResource("rel.txt")
      assert(url.getPath.endsWith("rel.txt"))
      assertThrows[MissingResourceException](getClass.findResource("nonexisting"))
   }
