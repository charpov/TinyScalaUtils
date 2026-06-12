package tinyscalautils.io

import org.scalatest.funsuite.AnyFunSuite

import java.io.IOException
import java.net.URI
import java.util.MissingResourceException

class ResourcesSuite extends AnyFunSuite:
   test("findResource absolute"):
      val url = this.findResource("/foo.txt")
      assert(url.getPath.endsWith("foo.txt"))
      assertThrows[MissingResourceException](this.findResource("nonexisting"))

   test("findResource relative"):
      val url = this.findResource("rel.txt")
      assert(url.getPath.endsWith("rel.txt"))
      assertThrows[MissingResourceException](this.findResource("nonexisting"))

   test("findResourceAsStream relative"):
      assert(read(this.findResourceAsStream("rel.txt")) == "line1\nline2\n")

   test("findResourceAsStream fallback relative"):
      val uri = URI.create("https://example.com")
      assert(read(this.findResourceAsStream(uri)("rel.txt")) == "line1\nline2\n")

   test("findResource fallback"):
      val url = this.findResource(URI.create("https://httpbin.org/anything/"))("fallback.txt")
      assert(url.getPath.endsWith("fallback.txt"))
      assert(url.openConnection().getContentLength > 0)

   test("findResourceAsStream"):
      assert(read(this.findResourceAsStream("/ok1.txt")) == "ok\n")
      assert(read(this.findResourceAsStream("/ok2.txt")) == "ok\n")

   test("findResourceAsStream fallback not found"):
      val uri = URI.create("https://httpbin.org/status/404")
      val e   = intercept[IOException](this.findResourceAsStream(uri)("fallback.txt"))
      assert(e.getMessage.endsWith("fallback.txt"))
