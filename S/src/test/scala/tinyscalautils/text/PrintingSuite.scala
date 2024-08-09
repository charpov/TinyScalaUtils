package tinyscalautils.text

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.newThread
import tinyscalautils.util.FastRandom

class PrintingSuite extends AnyFunSuite:
   private val threadName = "Joe"
   private val msg1       = 42
   private val msg2       = "foo"

   private val time =
      val at = "(" + threadName + """ )?at ([0-2]\d):([0-5]\d):([0-5]\d)\.\d\d\d: (.*?)"""
      (at + at).r

   private val demo =
      val at = "(" + threadName + """ )?at XX:XX:([0-5]\d)\.\d\d\d: (.*?)"""
      (at + at).r

   private def doPrint(mode: PrintingMode): String = printout:
      newThread("Joe"):
         if FastRandom.nextBoolean() then
            mode.printf("X%dX", msg1)
            mode.println(msg2)
         else
            printf("X%dX", msg1)(using mode)
            println(msg2)(using mode)
      .join()

   private def parse(str: String): Boolean =
      assert(str.last == '\n')
      val line = str.init
      line match
         case time(t1, h1, m1, s1, str1, t2, h2, m2, s2, str2) =>
            assert(t1 == t2)
            assert(h1 == h2 || m1 == "59" && s1 == "59")
            assert(m1 == m2 || s1 == "59")
            assert(str1 == s"X${msg1}X")
            assert(str2 == msg2)
            t1 ne null
         case demo(t1, s1, str1, t2, s2, str2) =>
            assert(t1 == t2)
            assert(s1 == s2)
            assert(str1 == s"X${msg1}X")
            assert(str2 == msg2)
            t1 ne null
         case _ => fail(s"no match for '$line'")

   test("StandardMode"):
      assert(doPrint(standardMode) == s"X${msg1}X$msg2\n")

   test("SilentMode"):
      assert(doPrint(silentMode).isEmpty)

   test("ThreadMode"):
      assert(doPrint(threadMode) == s"$threadName: X${msg1}X$threadName: $msg2\n")

   test("TimeMode"):
      assert(!parse(doPrint(timeMode)))

   test("TimeDemoMode"):
      assert(!parse(doPrint(timeDemoMode)))

   test("ThreadTimeMode"):
      assert(parse(doPrint(threadTimeMode)))

   test("ThreadTimeDemoMode"):
      assert(parse(doPrint(threadTimeDemoMode)))

   test("simpler imports"):
      import tinyscalautils.text.silentMode.println
      assert(printout(println("not displayed")).isEmpty)
