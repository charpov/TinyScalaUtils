package tinyscalautils.test.threads

import org.scalactic.{ Prettifier, source }
import tinyscalautils.assertions.require
import tinyscalautils.threads.{ Executors, MarkedThreadFactory, shutdownAndWait }

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContextExecutorService, Future }
import scala.util.{ Failure, Success, Try }

/** A variant of `withThreadsAndWait` suitable for testing.
  *
  * @param maxThreads
  *   the pool size; 0 means unlimited.
  *
  * @param terminationTimeout
  *   the maximum time (in seconds) for the thread pool to terminate after the future is completed.
  *
  * @param ignoreRejections
  *   when true, tasks submitted to the thread pool after shutdown are silently ignored (this
  *   happens occasionally with callbacks that take place after a future has completed)
  *
  * The test is failed if the thread pool does not terminate before the timeout. This is only if the
  * future is successful: if the future fails ''and'' shutdown times out, the failure of the future
  * is reported.
  *
  * If the thread pool fails to terminate in time ''or'' the testing thread is interrupted while
  * waiting, `shutdownNow` is invoked, with no further waiting for termination.
  *
  * Threads from the pool are ''marked''.
  *
  * @see
  *   MarkedThread
  *
  * @since 1.0
  */
@throws[InterruptedException]
def withMarkedThreads[A](
    maxThreads: Int = 0,
    terminationTimeout: Double = 1.0,
    ignoreRejections: Boolean = false
)(
    code: ExecutionContextExecutorService ?=> Future[A]
)(using Prettifier, source.Position): A =
   require(maxThreads >= 0, s"maxThreads cannot be negative, is $maxThreads")
   val exec =
      val executors =
         val e = Executors.withFactory(MarkedThreadFactory)
         if ignoreRejections then e.silent else e
      if maxThreads > 0 then executors.newThreadPool(maxThreads)
      else executors.newUnlimitedThreadPool()
   try
      Try(Await.result(code(using exec), Duration.Inf)) match
         case Success(value) =>
            assert(
              exec.shutdownAndWait(terminationTimeout, force = true),
              "executor failed to terminate"
            )
            value
         case Failure(ex) =>
            exec.shutdownAndWait(terminationTimeout, force = true)
            throw ex
   catch
      case ex: InterruptedException =>
         exec.shutdownNow()
         throw ex

/** Simple variant of `withMarkedThreads` with default values. */
@throws[InterruptedException]
def withMarkedThreads[A](
    code: ExecutionContextExecutorService ?=> Future[A]
)(using Prettifier, source.Position): A = withMarkedThreads()(code)
