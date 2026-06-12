package tinyscalautils.text

import org.scalatest.funsuite.AnyFunSuite

class CleanStringsSuite extends AnyFunSuite:

   test("cleanCRLF") {
      val dirty = "x\r\ny\nz\ru\r\n\n"
      val clean = "x\ny\nz\ru\n\n"
      assert(dirty.cleanCRLF === clean)
   }

   test("short") {
      val letters    = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
      val longString = letters + letters.toLowerCase
      assert(longString.short == letters + "abc...")
      assert(longString.short(10) == "ABCDEFG...")
      assert(longString.short(29) == letters + "...")
      assert(longString.short(3) == "...")
      assertThrows[IllegalArgumentException](longString.short(2))
   }

   test("pad") {
      val shortString = "X"
      assert(shortString.pad(5) == "    X")
      assert(shortString.pad(5, padding = '.') == "....X")
      assert(shortString.pad(1) == "X")
      assert(shortString.pad(0) == "X")
      assertThrows[IllegalArgumentException](shortString.pad(-1))
   }
