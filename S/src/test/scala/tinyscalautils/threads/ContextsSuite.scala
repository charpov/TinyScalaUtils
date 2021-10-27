package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.{ Assertion, Succeeded }
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.timing.{ sleep, timeOf }
import tinyscalautils.threads.shutdownAndWait
import scala.concurrent.{ Await, Future, Promise }
import scala.concurrent.duration.{ Duration, SECONDS }

class ContextsSuite extends AnyFunSuite with Tolerance:

   test("withContext") {
      val exec = Executors.newUnlimitedThreadPool()
      val time = withContext(exec) {
         Future {
            timeOf(sleep(1.0))
         }
      }
      assert(!exec.isShutdown)
      assert(exec.shutdownAndWait(1.0))
      assert(time === 1.0 +- 0.01)
   }

   test("withLocalContext") {
      val exec = Executors.newUnlimitedThreadPool()
      val time = withLocalContext(exec) {
         Future {
            timeOf(sleep(1.0))
         }
      }
      assert(exec.isShutdown)
      assert(exec.isTerminated)
      assert(time === 1.0 +- 0.1)
   }

   test("withLocalContext, exception") {
      object Ex extends Exception
      val exec = Executors.newUnlimitedThreadPool()
      val ex = intercept[Exception] {
         withLocalContext(exec) {
            Future {
               throw Ex
            }
         }
      }
      assert(exec.isShutdown)
      assert(exec.isTerminated)
      assert(ex eq Ex)
   }

   test("withLocalContext, interrupt") {
      val exec = Executors.newUnlimitedThreadPool()
      val p    = Promise[Assertion]()
      val task: Runnable = () =>
         p.success {
            assertThrows[InterruptedException] {
               withLocalContext(exec) {
                  exec.execute((() => sleep(1.0)): Runnable)
                  Future.unit
               }
            }
         }
      val thread = Thread(task)
      thread.start()
      sleep(0.5)
      thread.interrupt()
      Await.ready(p.future, Duration.Inf)
      assert(exec.isShutdown)
      assert(exec.shutdownAndWait(1.0))
      assert(p.future.value.get.isSuccess)
   }
