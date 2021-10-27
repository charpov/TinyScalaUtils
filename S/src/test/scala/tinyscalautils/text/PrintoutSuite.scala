package tinyscalautils.text

import org.scalatest.funsuite.AnyFunSuite

import java.nio.charset.StandardCharsets

class PrintoutSuite extends AnyFunSuite:

   private def code() =
      Console.out.print("COP")
      System.out.print("SOP")
      Console.err.print("CEP")
      System.err.print("SEP")
      Console.out.printf("%s", "COF")
      System.out.printf("%s", "SOF")
      Console.err.printf("%s", "CEF")
      System.err.printf("%s", "SEF")
      Console.out.println("CON")
      System.out.println("SON")
      Console.err.println("CEN")
      System.err.println("SEN")

   test("printout") {
      assertResult("SOPCEPSEPSOFCEFSEFSON\nCEN\nSEN\n") {
         printout(true, true) {
            assert(printout(code()) == "COPCOFCON\n")
         }
      }
   }

   test("printout, includeErr") {
      assertResult("SOPSEPSOFSEFSON\nSEN\n") {
         printout(true, true) {
            assert(printout(includeErr = true)(code()) == "COPCEPCOFCEFCON\nCEN\n")
         }
      }
   }

   test("printout, includeSystem") {
      assertResult("CEPSEPCEFSEFCEN\nSEN\n") {
         printout(true, true) {
            assert(printout(includeSystem = true)(code()) == "COPSOPCOFSOFCON\nSON\n")
         }
      }
   }

   test("printout, includeErr, includeSystem") {
      assertResult("") {
         printout(true, true) {
            assert(
              printout(includeErr = true, includeSystem = true)(code())
                 == "COPSOPCEPSEPCOFSOFCEFSEFCON\nSON\nCEN\nSEN\n"
            )
         }
      }
   }

   private class E extends Exception

   test("exception") {
      val sout = System.out
      val serr = System.err
      val out  = Console.out
      val err  = Console.err
      assertThrows[E](printout(includeErr = true, includeSystem = true)(throw E()))
      assert(System.out eq sout)
      assert(System.err eq serr)
      assert(Console.out eq out)
      assert(Console.err eq err)
   }

   test("exception, nested") {
      val sout = System.out
      val serr = System.err
      val out  = Console.out
      val err  = Console.err
      assertThrows[E] {
         printout(includeErr = true, includeSystem = true) {
            printout(includeErr = true, includeSystem = true)(throw E())
         }
      }
      assert(System.out eq sout)
      assert(System.err eq serr)
      assert(Console.out eq out)
      assert(Console.err eq err)
   }

   test("example 1") {
      Console.withErr(java.io.OutputStream.nullOutputStream()) {
         val str = printout {
            Console.out.print("hello")
            Console.err.println(" world")
         } // the string "hello"
         assert(str == "hello")
      }
   }

   test("example 2") {
      val str = printout(includeErr = true) {
         Console.out.print("hello")
         Console.err.println(" world")
      } // the string "hello world\n"
      assert(str == "hello world\n")
   }

   test("example 3") {
      val str = printout(includeSystem = true) {
         Console.out.print("hello")
         System.out.println(" world")
      } // the string "hello world\n"
      assert(str == "hello world\n")
   }
