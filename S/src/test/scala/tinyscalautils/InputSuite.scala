package tinyscalautils

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.assertions.requireState
import io.readAll
import io.noParsing
import io.readingAll
import io.read
import java.io.IOException
import java.nio.file.{ AccessDeniedException, Files, Path }
import java.util
import scala.io.Source
import scala.util.Using

class InputSuite extends AnyFunSuite:
   requireState(Path.of("").toAbsolutePath.getFileName.toString == "S")

   val dir  = Path.of("src", "test", "resources")
   val path = dir.resolve("nums.txt")
   val uri  = path.toUri
   val url  = uri.toURL
   val name = path.toString
   val file = path.toFile

   val nums = 1 to 5

   def parse(line: String) = line.split(' ').flatMap(_.toIntOption).iterator

   test("demo 1"):
      import io.FileIsInput
      val numsSeq: IndexedSeq[Int]          = readAll(IndexedSeq)(file, parse)
      val nonBlankLines: IndexedSeq[String] = readAll(IndexedSeq)(file)
      val allLines: IndexedSeq[String]      = readAll(IndexedSeq)(file, noParsing)
      val numsList: List[Int]               = readAll(file, parse)
      val nonBlankLinesList: List[String]   = readAll(file)
      val allLinesList: List[String]        = readAll(file, noParsing)
      assert(numsSeq == nums)
      assert(nonBlankLines.length == 5)
      assert(allLines.length == 7)
      assert(numsList == nums)
      assert(nonBlankLinesList.length == 5)
      assert(allLinesList.length == 7)

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

   test("readAll InputStream, indexed seq"):
      import io.InputStreamIsInput
      val seq: IndexedSeq[Int] = readAll(IndexedSeq)(Files.newInputStream(path), parse)
      assert(seq == nums)

   test("readAll InputStream, indexed seq, default parser"):
      import io.InputStreamIsInput
      val seq: IndexedSeq[String] = readAll(IndexedSeq)(Files.newInputStream(path))
      assert(seq.length == 5)

   test("readAll InputStream, list"):
      import io.InputStreamIsInput
      val list: List[Int] = readAll(Files.newInputStream(path), parse)
      assert(list == nums)

   test("readAll InputStream, list, default parser"):
      import io.InputStreamIsInput
      val list: List[String] = readAll(Files.newInputStream(path))
      assert(list.length == 5)

   test("readAll Path, indexed seq"):
      import io.PathIsInput
      val seq: IndexedSeq[Int] = readAll(IndexedSeq)(path, parse)
      assert(seq == nums)

   test("readAll Path, indexed seq, default parser"):
      import io.PathIsInput
      val seq: IndexedSeq[String] = readAll(IndexedSeq)(path)
      assert(seq.length == 5)

   test("readAll Path, list"):
      import io.PathIsInput
      val list: List[Int] = readAll(path, parse)
      assert(list == nums)

   test("readAll Path, list, default parser"):
      import io.PathIsInput
      val list: List[String] = readAll(path)
      assert(list.length == 5)

   test("readAll URL, indexed seq"):
      import io.URLIsInput
      val seq: IndexedSeq[Int] = readAll(IndexedSeq)(url, parse)
      assert(seq == nums)

   test("readAll URL, indexed seq, default parser"):
      import io.URLIsInput
      val seq: IndexedSeq[String] = readAll(IndexedSeq)(url)
      assert(seq.length == 5)

   test("readAll URL, list"):
      import io.URLIsInput
      val list: List[Int] = readAll(url, parse)
      assert(list == nums)

   test("readAll URL, list, default parser"):
      import io.URLIsInput
      val list: List[String] = readAll(url)
      assert(list.length == 5)

   test("readAll URI, indexed seq"):
      import io.URIIsInput
      val seq: IndexedSeq[Int] = readAll(IndexedSeq)(uri, parse)
      assert(seq == nums)

   test("readAll URI, indexed seq, default parser"):
      import io.URIIsInput
      val seq: IndexedSeq[String] = readAll(IndexedSeq)(uri)
      assert(seq.length == 5)

   test("readAll URI, list"):
      import io.URIIsInput
      val list: List[Int] = readAll(uri, parse)
      assert(list == nums)

   test("readAll URI, list, default parser"):
      import io.URIIsInput
      val list: List[String] = readAll(uri)
      assert(list.length == 5)

   test("readAll name, indexed seq"):
      import io.FileNameIsInput
      val seq: IndexedSeq[Int] = readAll(IndexedSeq)(name, parse)
      assert(seq == nums)

   test("readAll name, indexed seq, default parser"):
      import io.FileNameIsInput
      val seq: IndexedSeq[String] = readAll(IndexedSeq)(name)
      assert(seq.length == 5)

   test("readAll name, list"):
      import io.FileNameIsInput
      val list: List[Int] = readAll(name, parse)
      assert(list == nums)

   test("readAll name, list, default parser"):
      import io.FileNameIsInput
      val list: List[String] = readAll(name)
      assert(list.length == 5)

   test("readAll URL string, indexed seq"):
      import io.URLStringIsInput
      val seq: IndexedSeq[Int] = readAll(IndexedSeq)(url.toString, parse)
      assert(seq == nums)

   test("readAll URL string, indexed seq, default parser"):
      import io.URLStringIsInput
      val seq: IndexedSeq[String] = readAll(IndexedSeq)(url.toString)
      assert(seq.length == 5)

   test("readAll URL string, list"):
      import io.URLStringIsInput
      val list: List[Int] = readAll(url.toString, parse)
      assert(list == nums)

   test("readAll URL string, list, default parser"):
      import io.URLStringIsInput
      val list: List[String] = readAll(url.toString)
      assert(list.length == 5)

   test("readAll file, indexed seq"):
      import io.FileIsInput
      val seq: IndexedSeq[Int] = readAll(IndexedSeq)(file, parse)
      assert(seq == nums)

   test("readAll file, indexed seq, default parser"):
      import io.FileIsInput
      val seq: IndexedSeq[String] = readAll(IndexedSeq)(file)
      assert(seq.length == 5)

   test("readAll file, list"):
      import io.FileIsInput
      val list: List[Int] = readAll(file, parse)
      assert(list == nums)

   test("readAll file, list, default parser"):
      import io.FileIsInput
      val list: List[String] = readAll(file)
      assert(list.length == 5)

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
