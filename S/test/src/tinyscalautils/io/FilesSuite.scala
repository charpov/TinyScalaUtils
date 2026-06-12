package tinyscalautils.io

import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.{ AccessDeniedException, Files, Paths }
import java.util

class FilesSuite extends AnyFunSuite:
   val dir     = Paths.get(this.findResource("/foo.txt").toURI).getParent
   val files   = Set("foo.txt", "bar.txt", "nums.txt", "ok1.txt", "ok2.txt.gz")
   val subdirs = Set("tinyscalautils")

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
