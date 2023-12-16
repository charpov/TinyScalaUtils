package tinyscalautils.control

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.{Executors, withThreadPoolAndWait}

import scala.util.Try
import tinyscalautils.lang.StackOverflowException
import tinyscalautils.timing.sleep

import java.util.concurrent.ThreadFactory
import scala.concurrent.Future

class NoStackOverflowSuite extends AnyFunSuite:
   def f(x: Int): Int = 1 + f(x + 1)

   test("in Try"):
      assertThrows[StackOverflowError](Try(f(0)))
      val t = Try(noStackOverflow(f(0)))
      assert(t.isFailure)
      assert(t.failed.get.isInstanceOf[StackOverflowException])

   test("in Future"):
      val quiet: ThreadFactory = r =>
         val t = Thread(r)
         t.setUncaughtExceptionHandler:
            case (_, _: StackOverflowError) => ()
            case (t, e) => Thread.getDefaultUncaughtExceptionHandler.uncaughtException(t, e)
         t
      val exec = Executors.withFactory(quiet).newUnlimitedThreadPool()
      withThreadPoolAndWait(exec, shutdown = true):
         val f1 = Future(f(0))
         val f2 = Future(noStackOverflow(f(0)))
         f2.failed.map: ex =>
            assert(ex.isInstanceOf[StackOverflowException])
            sleep(0.5)
            assert(!f1.isCompleted)
