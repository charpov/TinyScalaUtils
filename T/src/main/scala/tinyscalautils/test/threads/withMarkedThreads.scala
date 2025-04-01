package tinyscalautils.test.threads

import tinyscalautils.assertions.require
import tinyscalautils.threads.{ Executors, MarkedThreadFactory, shutdownAndWait }

import java.util.concurrent.TimeoutException
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
  * @throws TimeoutException
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
transparent inline def withMarkedThreads[A](maxThreads: Int, terminationTimeout: Double)(
    code: ExecutionContextExecutorService ?=> A
) =
   require(maxThreads > 0, s"pool size must be positive, not $maxThreads")
   _withMarkedThreads(maxThreads, terminationTimeout)(exec => code(using exec))

/** Same as `withMarkedThreads(maxThreads, terminationTimeout = 1.0)`. */
@throws[InterruptedException]
transparent inline def withMarkedThreads[A](maxThreads: Int)(
    code: ExecutionContextExecutorService ?=> A
) =
   require(maxThreads > 0, s"pool size must be positive, not $maxThreads")
   _withMarkedThreads(maxThreads, 1.0)(exec => code(using exec))

/** Same as `withMarkedThreads(<infinity>, terminationTimeout)`. */
@throws[InterruptedException]
transparent inline def withMarkedThreads[A](terminationTimeout: Double)(
    code: ExecutionContextExecutorService ?=> A
) = _withMarkedThreads(0, terminationTimeout)(exec => code(using exec))

/** Same as `withMarkedThreads(<infinity>, terminationTimeout = 1.0)`. */
@throws[InterruptedException]
transparent inline def withMarkedThreads[A](code: ExecutionContextExecutorService ?=> A) =
   _withMarkedThreads(0, 1.0)(exec => code(using exec))

private transparent inline def _withMarkedThreads[A](maxThreads: Int, terminationTimeout: Double)(
    code: ExecutionContextExecutorService => A
) =
   require(
     terminationTimeout >= 0.0,
     s"termination timeout must be non-negative, not $terminationTimeout"
   )
   val exec =
      val executors = Executors.withFactory(MarkedThreadFactory).silent
      if maxThreads > 0 then executors.newThreadPool(maxThreads)
      else executors.newUnlimitedThreadPool()
   inline code match
      case futureCode: (ExecutionContextExecutorService => Future[?]) =>
         _withMarkedThreadsAndWait(exec, terminationTimeout)(futureCode)
      case _ => _withMarkedThreadsAndWait(exec, terminationTimeout)(e => { code(e); Future.unit })

private def _withMarkedThreadsAndWait[A](
    exec: ExecutionContextExecutorService,
    terminationTimeout: Double
)(code: ExecutionContextExecutorService => Future[A]) =
   val actualTimeout =
      if terminationTimeout == 0.0 then Double.PositiveInfinity else terminationTimeout
   val value =
      try Await.result(code(exec), Duration.Inf)
      catch
         case ex: InterruptedException =>
            exec.shutdownNow()
            throw ex
         case NonFatal(ex) =>
            exec.shutdownAndWait(actualTimeout, force = true)
            throw ex
   if exec.shutdownAndWait(actualTimeout, force = true) then value
   else throw TimeoutException("executor failed to terminate")
