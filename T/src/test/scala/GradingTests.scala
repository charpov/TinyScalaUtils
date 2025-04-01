import org.scalactic.{ Prettifier, SizeLimit, Tolerance }
import org.scalatest.{ Args, ConfigMap }
import org.scalatest.events.{ TestCanceled, TestFailed }
import org.scalatest.exceptions.TestFailedException
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow
import org.scalatest.time.SpanSugar.convertDoubleToGrainOfTime
import tinyscalautils.lang.{ InterruptibleConstructor, InterruptibleEquality }
import tinyscalautils.test.grading.Grading
import tinyscalautils.test.tagobjects.{ Async, Fail }
import tinyscalautils.threads.Executors.global
import tinyscalautils.threads.runAsync
import tinyscalautils.timing.{ getTime, sleep, timeOf }
import tinyscalautils.lang.unit
import tinyscalautils.test.text.TruncatingPrettifier

class GradingTests extends AnyFunSuite with Tolerance:
   private val nums = List.range(0, 42)

   test("prettifier and grade"):
      class Tests(using Prettifier) extends AnyFunSuite with Grading:
         test("default prettifier"):
            val e = intercept[TestFailedException](assert(nums.isEmpty))
            assert(e.message.exists(_.endsWith(", 31, ...) was not empty")))

         test("overridden prettifier"):
            given Prettifier = Prettifier.truncateAt(SizeLimit(10))

            val e = intercept[TestFailedException](assert(nums.isEmpty))
            assert(e.message.exists(_.endsWith(", 9, ...) was not empty")))

         test("failed [3pts]")(fail())
      end Tests

      val suite = Tests(using Prettifier.truncateAt(SizeLimit(32)))
      suite.run(None, silent)
      assert(suite.grader.grade == 0.4)

   test("truncating default prettifier and grade"):
      class Tests(using Prettifier) extends AnyFunSuite with Grading:
         test("prettifier"):
            val e = intercept[TestFailedException](assert(nums.isEmpty))
            assert(e.message.exists(_.endsWith(", 10, 1... was not empty")))

      val suite = Tests(using TruncatingPrettifier(43))
      suite.run(None, silent)
      assert(suite.grader.grade == 1.0)

   test("truncating prettifier and grade"):
      given Prettifier = Prettifier.truncateAt(SizeLimit(5))

      class Tests(using Prettifier) extends AnyFunSuite with Grading:
         test("prettifier"):
            val e = intercept[TestFailedException](assert(nums.isEmpty))
            assert(e.message.exists(_.endsWith(", 4, ... was not empty")))

      val suite = Tests(using TruncatingPrettifier(23))
      suite.run(None, silent)
      assert(suite.grader.grade == 1.0)

   test("timeout"):
      class Tests extends AnyFunSuite with Grading:
         test("the test")(sleep(2.0))

      val suite = Tests()
      val time  = timeOf(suite.run(None, silent))
      assert(time === 1.0 +- 0.1)
      assert(suite.grader.grade == 0.0)

   test("interruptible construction"):
      class C extends InterruptibleConstructor

      class Tests extends AnyFunSuite with Grading:
         test("the test"):
            while true do if C().## == getTime() then fail()

      val suite = Tests()
      val time  = timeOf(suite.run(None, silent))
      assert(time === 1.0 +- 0.1)
      assert(suite.grader.grade == 0.0)

   test("interruptible equality"):
      class C extends InterruptibleEquality

      class Tests extends AnyFunSuite with Grading:
         test("the test"):
            while true do if C().## == getTime() then fail()

      val suite = Tests()
      val time  = timeOf(suite.run(None, silent))
      assert(time === 1.0 +- 0.1)
      assert(suite.grader.grade == 0.0)

   test("runAsync"):
      class Tests extends AnyFunSuite with Grading:
         override val shortTimeLimit = 0.5.seconds

         test("test 1"):
            runAsync:
               val end = getTime() + 1E9 // 1 second
               while getTime() < end do Thread.onSpinWait()
         test("test 2") {}
      end Tests

      val suite = Tests()
      val time  = timeOf(assert(!suite.run(None, silent).succeeds()))
      assert(time === 0.5 +- 0.1)

   test("Async object"):
      class Tests extends AnyFunSuite with Grading:
         override val shortTimeLimit = 0.5.seconds
         override val longTimeLimit  = 1.second

         test("stuck", Async):
            val end = getTime() + 1E9 // 1 second
            while getTime() < end do Thread.onSpinWait()

         test("stuck, slow", Async, Slow):
            val end = getTime() + 2E9 // 2 seconds
            while getTime() < end do Thread.onSpinWait()
      end Tests

      val time1 = timeOf(assert(!Tests().run(Some("stuck"), silent).succeeds()))
      assert(time1 === 0.5 +- 0.1)
      val time2 = timeOf(assert(!Tests().run(Some("stuck, slow"), silent).succeeds()))
      assert(time2 === 1.0 +- 0.1)

   test("Async annotation"):
      @tinyscalautils.test.tags.Async
      class Tests extends AnyFunSuite with Grading:
         override val shortTimeLimit = 0.5.seconds
         override val longTimeLimit  = 1.second

         test("stuck"):
            val end = getTime() + 1E9 // 1 second
            while getTime() < end do Thread.onSpinWait()

         test("stuck, slow", Slow):
            val end = getTime() + 2E9 // 2 seconds
            while getTime() < end do Thread.onSpinWait()
      end Tests

      val time1 = timeOf(assert(!Tests().run(Some("stuck"), silent).succeeds()))
      assert(time1 === 0.5 +- 0.1)
      val time2 = timeOf(assert(!Tests().run(Some("stuck, slow"), silent).succeeds()))
      assert(time2 === 1.0 +- 0.1)

   test("Fail"):
      class Tests extends AnyFunSuite with Grading:
         var testHasRun = false
         test("stuck", Fail):
            testHasRun = true
      end Tests

      val suite = Tests()
      assert(!suite.run(None, silent).succeeds())
      assert(!suite.testHasRun)

   test("Fail annotation"):
      @tinyscalautils.test.tags.Fail
      class Tests extends AnyFunSuite with Grading:
         var testHasRun = false
         test("stuck"):
            testHasRun = true
      end Tests

      val suite = Tests()
      assert(!suite.run(None, silent).succeeds())
      assert(!suite.testHasRun)

   test("Fail(message)"):
      class Tests extends AnyFunSuite with Grading:
         var testHasRun = false
         test("stuck", Fail("bad")):
            testHasRun = true
      end Tests

      val suite = Tests()
      assert(!suite.run(None, Args(R)).succeeds())
      assert(!suite.testHasRun)
      R.lastEvent match
         case Some(ev: TestFailed) => assert(ev.message == "bad")
         case other                => fail(s"unexpected: $other ")

   test("Fail broken"):
      class Tests extends AnyFunSuite with Grading:
         var testHasRun = false
         test("stuck", Fail("good"), Fail("bad")):
            testHasRun = true
      end Tests

      val suite = Tests()
      assert(suite.run(None, Args(R)).succeeds())
      assert(!suite.testHasRun)
      R.lastEvent match
         case Some(ev: TestCanceled) => assert(ev.message.startsWith("conflicting tags"))
         case other                  => fail(s"unexpected: $other ")

   test("config map"):
      class Tests extends AnyFunSuite with Grading:
         test("the (test)")(unit)
      end Tests

      val config1 = ConfigMap("failed" -> Set("test"))
      val config2 = ConfigMap("failed" -> Set("the (test)"))
      val config3 = ConfigMap("failed" -> Set(".*test.*"))
      val config4 = ConfigMap("failed" -> Set(""""the (test)""""))

      assert(Tests().run(None, Args(R).copy(configMap = config1)).succeeds())
      assert(Tests().run(None, Args(R).copy(configMap = config2)).succeeds())
      assert(!Tests().run(None, Args(R).copy(configMap = config3)).succeeds())
      assert(!Tests().run(None, Args(R).copy(configMap = config4)).succeeds())

      R.lastEvent match
         case Some(ev: TestFailed) => assert(ev.message == """test name in "failed" set""")
         case other                => fail(s"unexpected: $other ")
