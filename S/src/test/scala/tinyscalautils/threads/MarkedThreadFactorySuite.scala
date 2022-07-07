package tinyscalautils.threads

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.isMarkedThread

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContext, ExecutionContextExecutorService, Future }

class MarkedThreadFactorySuite extends AnyFunSuite:

   private val noOp: Runnable = () => ()

   test("marked threads") {
      given exec: ExecutionContextExecutorService =
         Executors.withFactory(MarkedThreadFactory).newThreadPool(4)

      val future = Future(Thread.currentThread())
      val runner = Await.result(future, Duration.Inf)
      assert(runner.isInstanceOf[MarkedThread])
      assert(runner.isMarkedThread)
      exec.shutdown()
   }
