package tinyscalautils.test.threads

import tinyscalautils.assertions.require
import tinyscalautils.control.before
import tinyscalautils.threads.{ Executors, MarkedThreadFactory, shutdownAndWait }

import java.util.concurrent.TimeoutException
import scala.compiletime.summonFrom
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContextExecutorService, Future }
import scala.util.control.NonFatal

/** A variant of `withThreads` suitable for testing.
  *
  * The thread pool is shutdown after the code argument has run and waited for termination (even if
  * the code fails). If the thread pool fails to terminate in time _or_ the testing thread is
  * interrupted while waiting, `shutdownNow` is invoked, with no further waiting for termination.
  *
  * Threads from the pool are _marked_.
  *
  * @throws java.util.concurrent.TimeoutException
  *   if the thread pool does not terminate before the timeout, unless the code argument itself
  *   fails, in which case its failure is reported instead.
  *
  * @param maxThreads
  *   the pool size; must be positive.
  *
  * @param terminationTimeout
  *   the maximum time (in seconds) for the thread pool to terminate after shutdown; cannot be
  *   negative; 0.0 means wait forever.
  *
  * @see
  *   MarkedThread
  */
@throws[InterruptedException]
transparent inline def withMarkedThreads[A](maxThreads: Int, terminationTimeout: Double)
   (code: ExecutionContextExecutorService ?=> A) =
   require(maxThreads > 0, s"pool size must be positive, not $maxThreads")
   _withMarkedThreads(maxThreads, terminationTimeout, code)

/** Same as `withMarkedThreads(maxThreads, terminationTimeout = 1.0)`. */
@throws[InterruptedException]
transparent inline def withMarkedThreads[A](maxThreads: Int)
   (code: ExecutionContextExecutorService ?=> A) =
   require(maxThreads > 0, s"pool size must be positive, not $maxThreads")
   _withMarkedThreads(maxThreads, 1.0, code)

/** Same as `withMarkedThreads(<infinity>, terminationTimeout)`. */
@throws[InterruptedException]
transparent inline def withMarkedThreads[A](terminationTimeout: Double)
   (code: ExecutionContextExecutorService ?=> A) =
   _withMarkedThreads(0, terminationTimeout, code)

/** Same as `withMarkedThreads(<infinity>, terminationTimeout = 1.0)`. */
@throws[InterruptedException]
transparent inline def withMarkedThreads[A]()(code: ExecutionContextExecutorService ?=> A) =
   _withMarkedThreads(0, 1.0, code)

private transparent inline def _withMarkedThreads[A]
   (maxThreads: Int, timeout: Double, code: ExecutionContextExecutorService ?=> A) =
   require(timeout >= 0.0, s"termination timeout must be non-negative, not $timeout")
   val actualTimeout = if timeout == 0.0 then Double.PositiveInfinity else timeout

   val exec =
      val executors = Executors.withFactory(MarkedThreadFactory).silent
      if maxThreads > 0 then executors.newThreadPool(maxThreads)
      else executors.newUnlimitedThreadPool()

   val futureCode =
      summonFrom:
         case ev: (A <:< Future[?]) => () => ev(code(using exec))
         case _                     => () => { code(using exec); Future.unit }

   (
     try Await.result(futureCode(), Duration.Inf)
     catch
        case ex: InterruptedException =>
           exec.shutdownNow()
           throw ex
        case NonFatal(ex) =>
           exec.shutdownAndWait(actualTimeout, force = true)
           throw ex
   ) before:
      if !exec.shutdownAndWait(actualTimeout, force = true) then
         throw TimeoutException("executor failed to terminate")
