package tinyscalautils.io

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.assertions.require

import java.nio.file.{ AccessDeniedException, Files, Path }
import java.util

class FilesSuite extends AnyFunSuite:
   require(Path.of("").toAbsolutePath.getFileName.toString == "S")

   val files   = Set("foo.txt", "bar.txt", "nums.txt", "ok1.txt", "ok2.txt.gz")
   val subdirs = Set("tinyscalautils")
   val dir     = Path.of("src", "test", "resources")

   test("listPaths"):
      assert(listPaths(dir).map(_.getFileName.toString).toSet == files ++ subdirs)

   test("listPaths, errors"):
      val perms = Files.getPosixFilePermissions(dir)
      try
         Files.setPosixFilePermissions(dir, util.Collections.emptySet)
         assertThrows[AccessDeniedException](listPaths(dir))
         assert(listPaths(dir, silent = true).isEmpty)
      finally Files.setPosixFilePermissions(dir, perms)

   test("listPaths, argument"):
      assertThrows[IllegalArgumentException](listPaths(dir.resolve("foo.txt")))
