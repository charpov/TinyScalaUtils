package tinyscalautils.io

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow

import java.nio.file.{ Files, Path }
import java.util
import java.nio.file.AccessDeniedException
import java.io.{ IOException, UncheckedIOException }

class FilesSuite extends AnyFunSuite:
   assume(Path.of("").toAbsolutePath.getFileName.toString == "S")

   val files = Set("foo.txt", "bar.txt")
   val dir   = Path.of("src", "test", "resources")

   test("listPaths") {
      assert(listPaths(dir).map(_.getFileName.toString).toSet == files)
   }

   test("listPaths, errors") {
      val perms = Files.getPosixFilePermissions(dir)
      try
         Files.setPosixFilePermissions(dir, util.Collections.emptySet)
         assertThrows[AccessDeniedException](listPaths(dir))
         assert(listPaths(dir, silent = true).isEmpty)
      finally Files.setPosixFilePermissions(dir, perms)
   }

   test("listPaths, argument") {
      assertThrows[IllegalArgumentException] {
         listPaths(dir.resolve("foo.txt"))
      }
   }

   test("listLines") {
      for (f <- files) do assert(listLines(dir.resolve(f)) === List("line1", "line2"))
   }

   test("listLines, errors") {
      assertThrows[UncheckedIOException](listLines(dir))
      assert(listLines(dir, silent = true).isEmpty)
      val perms = Files.getPosixFilePermissions(dir)
      try
         Files.setPosixFilePermissions(dir, util.Collections.emptySet)
         assertThrows[AccessDeniedException](listLines(dir))
         assert(listLines(dir, silent = true).isEmpty)
      finally Files.setPosixFilePermissions(dir, perms)
   }
