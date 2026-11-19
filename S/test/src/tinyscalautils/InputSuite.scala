package tinyscalautils

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.io.*

import java.io.IOException
import java.nio.file.{ AccessDeniedException, Files }
import java.util
import scala.collection.Factory
import scala.collection.immutable.TreeSet
import scala.io.Source
import scala.util.Using
import scala.util.chaining.scalaUtilChainingOps

class InputSuite extends AnyFunSuite:
   val path = Files
      .createTempFile("nums", ".txt")
      .tap: p =>
         import io.PathIsOutput
         p.toFile.deleteOnExit()
         write(p)("none\n1\n2 3 4\n\n    \nnone\n5\n")

   val dir  = path.getParent
   val uri  = path.toUri
   val url  = uri.toURL
   val name = path.toString
   val file = path.toFile

   val nums = 1 to 5

   def parse(line: String) = line.split(' ').flatMap(_.toIntOption).iterator

   test("demo 1"):
      import io.FileIsInput
      val numsSeq: IndexedSeq[Int]             = readAllInto(IndexedSeq)(file, parse)
      val nonBlankLines: IndexedSeq[String]    = readAllInto(IndexedSeq)(file)
      val allLines: IndexedSeq[String]         = readAllInto(IndexedSeq)(file, noParsing)
      val numsDefault: Seq[Int]                = readAll(file, parse)
      val nonBlankLinesDefault: Seq[String]    = readAll(file)
      val allLinesDefault: Seq[String]         = readAll(file, noParsing)
      assert(numsSeq == nums)
      assert(nonBlankLines.length == 5)
      assert(allLines.length == 7)
      assert(numsDefault == nums)
      assert(nonBlankLinesDefault.length == 5)
      assert(allLinesDefault.length == 7)

   test("readAllInto, array factory"):
      import io.PathIsInput
      val factory: Factory[Int, Array[Int]] = Array
      val result: Array[Int] = readAllInto(factory)(path, parse)
      assert(result.toIndexedSeq == nums)
      val lineFactory: Factory[String, Array[String]] = Array
      val lines: Array[String] = readAllInto(lineFactory)(path)
      assert(lines.length == 5)
      assertThrows[IOException](readAllInto(factory)(dir, parse))
      assert(readAllInto(factory)(dir, parse, silent = true).isEmpty)

   test("readAllInto, sorted factory"):
      import io.PathIsInput
      val factory: Factory[Int, TreeSet[Int]] = TreeSet
      val result: TreeSet[Int] = readAllInto(factory)(path, line => parse(line).map(-_))
      assert(result.toList == List(-5, -4, -3, -2, -1))
      assert(readAllInto(factory)(dir, parse, silent = true).isEmpty)

   test("readAll, parser and silent"):
      import io.PathIsInput
      val result: Seq[Int] = readAll(path, parse, silent = false)
      assert(result == nums)
      assert(readAll(dir, parse, silent = true).isEmpty)
      assertThrows[IOException](readAll(dir, parse, silent = false))

   test("demo 2"):
      import io.FileIsInput
      Using.resource(readingAll(file, parse)): i =>
         assert(i.toSeq == nums)

   test("errors"):
      import io.PathIsInput
      assertThrows[IOException](readAll(dir))
      assert(readAll(dir, silent = true).isEmpty)
      val perms = Files.getPosixFilePermissions(path)
      try
         Files.setPosixFilePermissions(path, util.Collections.emptySet)
         assertThrows[AccessDeniedException](readAll(path))
         assert(readAll(path, silent = true).isEmpty)
      finally Files.setPosixFilePermissions(path, perms)

   test("no parsing"):
      import io.PathIsInput
      assert(readAll(path, noParsing).length == 7)

   test("readAllInto InputStream, indexed seq"):
      import io.InputStreamIsInput
      val seq: IndexedSeq[Int] = readAllInto(IndexedSeq)(Files.newInputStream(path), parse)
      assert(seq == nums)

   test("readAllInto InputStream, indexed seq, default parser"):
      import io.InputStreamIsInput
      val seq: IndexedSeq[String] = readAllInto(IndexedSeq)(Files.newInputStream(path))
      assert(seq.length == 5)

   test("readAll InputStream, seq"):
      import io.InputStreamIsInput
      val seq: Seq[Int] = readAll(Files.newInputStream(path), parse)
      assert(seq == nums)

   test("readAll InputStream, seq, default parser"):
      import io.InputStreamIsInput
      val seq: Seq[String] = readAll(Files.newInputStream(path))
      assert(seq.length == 5)

   test("readAllInto Path, indexed seq"):
      import io.PathIsInput
      val seq: IndexedSeq[Int] = readAllInto(IndexedSeq)(path, parse)
      assert(seq == nums)

   test("readAllInto Path, indexed seq, default parser"):
      import io.PathIsInput
      val seq: IndexedSeq[String] = readAllInto(IndexedSeq)(path)
      assert(seq.length == 5)

   test("readAll Path, seq"):
      import io.PathIsInput
      val seq: Seq[Int] = readAll(path, parse)
      assert(seq == nums)

   test("readAll Path, seq, default parser"):
      import io.PathIsInput
      val seq: Seq[String] = readAll(path)
      assert(seq.length == 5)

   test("readAllInto URL, indexed seq"):
      import io.URLIsInput
      val seq: IndexedSeq[Int] = readAllInto(IndexedSeq)(url, parse)
      assert(seq == nums)

   test("readAllInto URL, indexed seq, default parser"):
      import io.URLIsInput
      val seq: IndexedSeq[String] = readAllInto(IndexedSeq)(url)
      assert(seq.length == 5)

   test("readAll URL, seq"):
      import io.URLIsInput
      val seq: Seq[Int] = readAll(url, parse)
      assert(seq == nums)

   test("readAll URL, seq, default parser"):
      import io.URLIsInput
      val seq: Seq[String] = readAll(url)
      assert(seq.length == 5)

   test("readAllInto URI, indexed seq"):
      import io.URIIsInput
      val seq: IndexedSeq[Int] = readAllInto(IndexedSeq)(uri, parse)
      assert(seq == nums)

   test("readAllInto URI, indexed seq, default parser"):
      import io.URIIsInput
      val seq: IndexedSeq[String] = readAllInto(IndexedSeq)(uri)
      assert(seq.length == 5)

   test("readAll URI, seq"):
      import io.URIIsInput
      val seq: Seq[Int] = readAll(uri, parse)
      assert(seq == nums)

   test("readAll URI, seq, default parser"):
      import io.URIIsInput
      val seq: Seq[String] = readAll(uri)
      assert(seq.length == 5)

   test("readAllInto name, indexed seq"):
      import io.FileNameIsInput
      val seq: IndexedSeq[Int] = readAllInto(IndexedSeq)(name, parse)
      assert(seq == nums)

   test("readAllInto name, indexed seq, default parser"):
      import io.FileNameIsInput
      val seq: IndexedSeq[String] = readAllInto(IndexedSeq)(name)
      assert(seq.length == 5)

   test("readAll name, seq"):
      import io.FileNameIsInput
      val seq: Seq[Int] = readAll(name, parse)
      assert(seq == nums)

   test("readAll name, seq, default parser"):
      import io.FileNameIsInput
      val seq: Seq[String] = readAll(name)
      assert(seq.length == 5)

   test("readAllInto URL string, indexed seq"):
      import io.URLStringIsInput
      val seq: IndexedSeq[Int] = readAllInto(IndexedSeq)(url.toString, parse)
      assert(seq == nums)

   test("readAllInto URL string, indexed seq, default parser"):
      import io.URLStringIsInput
      val seq: IndexedSeq[String] = readAllInto(IndexedSeq)(url.toString)
      assert(seq.length == 5)

   test("readAll URL string, seq"):
      import io.URLStringIsInput
      val seq: Seq[Int] = readAll(url.toString, parse)
      assert(seq == nums)

   test("readAll URL string, seq, default parser"):
      import io.URLStringIsInput
      val seq: Seq[String] = readAll(url.toString)
      assert(seq.length == 5)

   test("readAllInto file, indexed seq"):
      import io.FileIsInput
      val seq: IndexedSeq[Int] = readAllInto(IndexedSeq)(file, parse)
      assert(seq == nums)

   test("readAllInto file, indexed seq, default parser"):
      import io.FileIsInput
      val seq: IndexedSeq[String] = readAllInto(IndexedSeq)(file)
      assert(seq.length == 5)

   test("readAll file, seq"):
      import io.FileIsInput
      val seq: Seq[Int] = readAll(file, parse)
      assert(seq == nums)

   test("readAll file, seq, default parser"):
      import io.FileIsInput
      val seq: Seq[String] = readAll(file)
      assert(seq.length == 5)

   test("readingAll"):
      import io.SourceIsInput
      val in     = Files.newInputStream(path)
      var closed = false
      val source = Source
         .fromFile(file)
         .withClose: () =>
            in.close()
            closed = true
      Using.resource(readingAll(source, parse)): i =>
         assert(i.hasNext)
         assert(i.next() == 1)
         assert(i.next() == 2)
         assert(i.hasNext)
      assert(closed)

   test("read"):
      import io.PathIsInput
      assert(read(path) == "none\n1\n2 3 4\n\n    \nnone\n5\n")
