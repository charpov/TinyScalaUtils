package tinyscalautils.threads

import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.Future

class MarkedThreadFactorySuite extends AnyFunSuite:
   test("marked threads"):
      val runner =
         withThreadPoolAndWait(
           Executors.withFactory(MarkedThreadFactory).newThreadPool(4),
           shutdown = true
         )(Future(Thread.currentThread()))
      assert(runner.isInstanceOf[MarkedThread])
      assert(runner.isMarkedThread)
