package tinyscalautils.test.threads

import org.scalactic.{ Prettifier, source }
import tinyscalautils.threads.{ Executors, shutdownAndWait }
import tinyscalautils.assertions.require

import scala.util.{ Failure, Success, Try }
import java.util.concurrent.ExecutorService
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContextExecutorService, Future }

/** A variant of `withLocalThreadPool` with a timeout on pool shutdown.
  *
  * Same as `tinyscalautils.threads.withLocalThreadPool` except that the test is failed if the
  * thread pool does not terminate before the timeout. This is only if the future is successful: if
  * the future fails ''and'' shutdown times out, the failure of the future is reported.
  */
def withLocalThreadPool[A, Exec <: ExecutorService](exec: Exec, timeout: Double = 1.0)(
    code: Exec ?=> Future[A]
)(using Prettifier, source.Position): A =
   try
      Try(Await.result(code(using exec), Duration.Inf)) match
         case Success(value) =>
            assert(exec.shutdownAndWait(timeout), "executor failed to terminate")
            value
         case Failure(ex) =>
            if !exec.shutdownAndWait(timeout) then exec.shutdownNow()
            throw ex
   catch
      case ex: InterruptedException =>
         exec.shutdownNow()
         throw ex

def withLocalThreads[A](maxThreads: Int = 0, timeout: Double = 1.0)(
    code: ExecutionContextExecutorService ?=> Future[A]
)(using Prettifier, source.Position): A =
   require(maxThreads >= 0, s"maxThreads cannot be negative, is $maxThreads")
   val exec =
      if maxThreads > 0 then Executors.newThreadPool(maxThreads)
      else Executors.newUnlimitedThreadPool()
   withLocalThreadPool(exec, timeout)(code)
