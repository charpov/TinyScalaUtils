import org.scalactic.Tolerance
import org.scalatest.events.TestCanceled
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow
import org.scalatest.time.SpanSugar.{ convertDoubleToGrainOfTime, convertIntToGrainOfTime }
import org.scalatest.{ Args, Tag }
import tinyscalautils.test.mixins.DualTimeLimits
import tinyscalautils.test.tagobjects.*
import tinyscalautils.timing.sleep

class DualTimeLimitsTests extends AnyFunSuite with Tolerance:
   test("Fast/Slow/Timeout/NoTimeout"):
      class Tests extends AnyFunSuite with DualTimeLimits:
         override val shortTimeLimit = 0.5.seconds
         override val longTimeLimit  = 1.second

         test("fast, successful 1")(sleep(0.4))
         test("fast, successful 2", NoTag)(sleep(0.4))
         test("slow, successful", Slow)(sleep(0.6))
         test("timeout, successful", Timeout(1.5))(sleep(1.1))
         test("no timeout 1", NoTimeout)(sleep(1.1))
         test("no timeout 2", Slow, NoTimeout)(sleep(1.1))
         test("no timeout 3", Timeout(0.5), NoTimeout)(sleep(1.1))
         test("no timeout 4", NoTag, NoTimeout)(sleep(1.1))
         test("fast, failed 1")(sleep(0.6))
         test("fast, failed 2", NoTag)(sleep(0.6))
         test("slow, failed", Slow)(sleep(1.1))
         test("timeout, failed", Timeout(0.5))(sleep(1.1))
      end Tests

      val suite = Tests()
      assert(suite.run(Some("fast, successful 1"), silent).succeeds())
      assert(suite.run(Some("fast, successful 2"), silent).succeeds())
      assert(suite.run(Some("slow, successful"), silent).succeeds())
      assert(suite.run(Some("timeout, successful"), silent).succeeds())
      assert(suite.run(Some("no timeout 1"), silent).succeeds())
      assert(suite.run(Some("no timeout 2"), silent).succeeds())
      assert(suite.run(Some("no timeout 3"), silent).succeeds())
      assert(suite.run(Some("no timeout 4"), silent).succeeds())
      assert(!suite.run(Some("fast, failed 1"), silent).succeeds())
      assert(!suite.run(Some("fast, failed 2"), silent).succeeds())
      assert(!suite.run(Some("slow, failed"), silent).succeeds())
      assert(!suite.run(Some("timeout, failed"), silent).succeeds())

   test("conflicting tags"):
      class Tests extends AnyFunSuite with DualTimeLimits:
         override val shortTimeLimit = 0.5.seconds
         override val longTimeLimit  = 1.second

         test("Timeout + Timeout", Timeout(2), Timeout(1)) {}
         test("Slow + Timeout", Slow, Timeout(1)) {}
      end Tests

      val suite = Tests()
      for str <- Seq("Timeout + Timeout", "Slow + Timeout") do
         assert(suite.run(Some(str), Args(R)).succeeds())
         R.lastEvent match
            case Some(ev: TestCanceled) => assert(ev.message.startsWith("conflicting tags:"))
            case other                  => fail(s"unexpected: $other ")

   test("Timeout broken"):
      class Tests(str: String) extends AnyFunSuite with DualTimeLimits:
         override val shortTimeLimit = 0.5.seconds
         override val longTimeLimit  = 1.second

         test("timeout, broken", Tag(str)) {}
      end Tests

      for end <- Seq("", "(X)", "X", "()", "( )", "(2", "23)", "(-1)") do
         val str = Timeout.name + end
         assert(Tests(str).run(Some("timeout, broken"), Args(R)).succeeds())
         R.lastEvent match
            case Some(ev: TestCanceled) => assert(ev.message == s"'$str' is not a valid tag")
            case other                  => fail(s"unexpected: $other ")

   test("Slow annotation"):
      @org.scalatest.tags.Slow
      class Tests extends AnyFunSuite with DualTimeLimits:
         override val shortTimeLimit = 0.5.seconds
         override val longTimeLimit  = 1.second

         test("slow")(sleep(0.6))
      end Tests

      assert(Tests().run(Some("slow"), silent).succeeds())

   test("NoTimeout annotation"):
      @tinyscalautils.test.tags.NoTimeout
      class Tests extends AnyFunSuite with DualTimeLimits:
         override val shortTimeLimit = 0.1.seconds
         override val longTimeLimit  = 0.5.seconds

         test("slow 1")(sleep(0.6))
         test("slow 2", Slow)(sleep(0.6))
      end Tests

      assert(Tests().run(Some("slow 1"), silent).succeeds())
      assert(Tests().run(Some("slow 2"), silent).succeeds())

   test("when/unless"):
      class Tests extends AnyFunSuite with DualTimeLimits:
         override val shortTimeLimit = 0.5.seconds
         override val longTimeLimit  = 2.seconds

         for time <- Seq(0.1, 0.2, 1.1, 1.2) do
            test(s"time 1: $time", Slow.when(time > 1.0))(sleep(time))
            test(s"time 2: $time", Slow.unless(time < 1.0))(sleep(time))
            test(s"time 3: $time", NoTimeout.when(time > 1.0))(sleep(time))
      end Tests

      val suite = Tests()
      assert(suite.run(None, silent).succeeds())
      assert(Slow.when(true) eq Slow.unless(false))
      assert(Slow.when(false) eq Slow.unless(true))
