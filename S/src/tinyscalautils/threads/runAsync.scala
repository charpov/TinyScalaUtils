package tinyscalautils.threads

import tinyscalautils.control.noStackOverflow

import java.util.concurrent.{ CancellationException, Executor }
import scala.concurrent.{ ExecutionContext, Promise }
import scala.util.Try

/** Runs code on the given pool and interrupts runner when interrupted. If interrupted while waiting
  * for code to complete, an `InterruptedException` is thrown.
  *
  * @since 1.1
  */
@throws[InterruptedException]
def runAsync[A](code: => A)(using exec: Executor | ExecutionContext): A =
   @volatile var runner: Thread = null
   @volatile var shouldRun      = true
   try
      withThreads(exec):
         val promise = Promise[A]()
         exec.run:
            runner = Thread.currentThread
            if shouldRun then promise.complete(Try(noStackOverflow(code)))
            else promise.failure(CancellationException())
         promise.future
   catch
      case e: InterruptedException =>
         shouldRun = false
         if runner ne null then runner.interrupt()
         throw e
