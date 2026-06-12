package tinyscalautils.text

import org.scalatest.funsuite.AnyFunSuite

class TimeStringSuite extends AnyFunSuite:
   private val max: Double = java.lang.Double.valueOf("0x1.517ffffea032fp47")

   test("demo"):
      assert(timeString(180.02) == "3 minutes, 20 milliseconds")
      assert(timeString(3609.732) == "1 hour, 10 seconds")
      assert(timeString(3609.732, unitsCount = 3) == "1 hour, 9 seconds, 732 milliseconds")
      assert(timeString(3609.732, unitsCount = 1) == "1 hour")

   test("exceptions"):
      assertThrows[IllegalArgumentException](timeString(-1))
      assertThrows[IllegalArgumentException](timeString(0, 0))
      assertThrows[IllegalArgumentException](timeString(math.nextUp(max)))

   test("large values"):
      assertResult("2147483647 days, 11 hours, 29 minutes, 29 seconds, 472 milliseconds"):
         timeString(max, unitsCount = 5)
      assert(timeString(max, unitsCount = 1) == "2147483647 days")

   test("multiple units"):
      for u <- 1 to 10 do
         assert(timeString(0, u) == "0 second")
         assert(timeString(math.nextDown(0.0005), u) == "0 second")
         assert(timeString(0.0005, u) == "1 millisecond")
         assert(timeString(2 * 86_400.0, u) == "2 days")
         assert(timeString(2 * 3_600.0, u) == "2 hours")
         assert(timeString(2 * 60, u) == "2 minutes")
         assert(timeString(2, u) == "2 seconds")
      for u <- 2 to 10 do
         assert(timeString(2.5, u) == "2 seconds, 500 milliseconds")
         assert(timeString(2.499, u) == "2 seconds, 499 milliseconds")
      for u <- 3 to 10 do
         assertResult("2 minutes, 29 seconds, 500 milliseconds"):
            timeString(2 * 60 + 29.5, u)
         assertResult("2 minutes, 29 seconds, 499 milliseconds"):
            timeString(2 * 60 + 29.499, u)
      for u <- 4 to 10 do
         assertResult("2 hours, 29 minutes, 29 seconds, 500 milliseconds"):
            timeString(2 * 3_600.0 + 29 * 60.0 + 29.5, u)
         assertResult("2 hours, 29 minutes, 29 seconds, 499 milliseconds"):
            timeString(2 * 3_600.0 + 29 * 60.0 + 29.499, u)
      for u <- 5 to 10 do
         assertResult("2 days, 11 hours, 29 minutes, 29 seconds, 500 milliseconds"):
            timeString(2 * 86_400.0 + 11 * 3_600.0 + 29 * 60.0 + 29.5, u)
         assertResult("2 days, 11 hours, 29 minutes, 29 seconds, 499 milliseconds"):
            timeString(2 * 86_400.0 + 11 * 3_600.0 + 29 * 60.0 + 29.499, u)

   test("units = 1"):
      assert(timeString(2 * 86_400.0 + 11 * 3_600.0 + 29 * 60.0 + 29.5, unitsCount = 1) == "3 days")
      assertResult("2 days"):
         timeString(2 * 86_400.0 + 11 * 3_600.0 + 29 * 60.0 + 29.499, unitsCount = 1)
      assert(timeString(2 * 3_600.0 + 29 * 60.0 + 29.5, unitsCount = 1) == "3 hours")
      assert(timeString(2 * 3_600.0 + 29 * 60.0 + 29.499, unitsCount = 1) == "2 hours")
      assert(timeString(2 * 60 + 29.5, unitsCount = 1) == "3 minutes")
      assert(timeString(2 * 60 + 29.499, unitsCount = 1) == "2 minutes")
      assert(timeString(2.5, unitsCount = 1) == "3 seconds")
      assert(timeString(2.499, unitsCount = 1) == "2 seconds")

   test("units = 2"):
      assert(timeString(2 * 86_400.0 + 11 * 3_600.0 + 29 * 60.0 + 29.5) == "2 days, 12 hours")
      assert(timeString(2 * 86_400.0 + 11 * 3_600.0 + 29 * 60.0 + 29.499) == "2 days, 11 hours")
      assert(timeString(2 * 3_600.0 + 29 * 60.0 + 29.5) == "2 hours, 30 minutes")
      assert(timeString(2 * 3_600.0 + 29 * 60.0 + 29.499) == "2 hours, 29 minutes")
      assert(timeString(2 * 60 + 29.5) == "2 minutes, 30 seconds")
      assert(timeString(2 * 60 + 29.499) == "2 minutes, 29 seconds")

   test("units = 3"):
      assertResult("2 days, 11 hours, 30 minutes"):
         timeString(2 * 86_400.0 + 11 * 3_600.0 + 29 * 60.0 + 29.5, unitsCount = 3)
      assertResult("2 days, 11 hours, 29 minutes"):
         timeString(2 * 86_400.0 + 11 * 3_600.0 + 29 * 60.0 + 29.499, unitsCount = 3)
      assertResult("2 hours, 29 minutes, 30 seconds"):
         timeString(2 * 3_600.0 + 29 * 60.0 + 29.5, unitsCount = 3)
      assertResult("2 hours, 29 minutes, 29 seconds"):
         timeString(2 * 3_600.0 + 29 * 60.0 + 29.499, unitsCount = 3)

   test("units = 4"):
      assertResult("2 days, 11 hours, 29 minutes, 30 seconds"):
         timeString(2 * 86_400.0 + 11 * 3_600.0 + 29 * 60.0 + 29.5, unitsCount = 4)
      assertResult("2 days, 11 hours, 29 minutes, 29 seconds"):
         timeString(2 * 86_400.0 + 11 * 3_600.0 + 29 * 60.0 + 29.499, unitsCount = 4)
