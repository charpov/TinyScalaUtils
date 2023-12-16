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

   private def parse(line: String): Option[Int] =
      Option.when(line.startsWith("line"))(line.substring(4).toIntOption).flatten

   test("parseURL list") {
      val list: List[Int] = parseURL(this.findResource("/foo.txt"), parse)
      assert(list == List(1, 2))
   }

   test("parseURL indexed seq") {
      val seq: IndexedSeq[Int] = parseURL(this.findResource("/foo.txt"), parse, IndexedSeq)
      assert(seq == Seq(1, 2))
   }
