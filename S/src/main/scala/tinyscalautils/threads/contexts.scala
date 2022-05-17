package tinyscalautils.threads

import java.util.concurrent.ExecutorService
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContextExecutorService, Future }
import scala.util.Try
import tinyscalautils.assertions.require

/** Runs code within an implicit execution context, and waits for future completion.
  *
  * For example:
  *
  * {{{
  * withContext(ExecutionContext.global) {
  *     val f = Future { ... }
  *     val g = f.map(...)
  *     g.filter(...)
  * }
  * }}}
  *
  * This construct waits for the future produced by the code to terminate. The execution context is
  * left as is.
  *
  * @since 1.0
  */
def withContext[A, Exec](exec: Exec)(code: Exec ?=> Future[A]): A =
   Await.result(code(using exec), Duration.Inf)

/** Runs code within a temporary implicit execution context, and waits for future completion.
  *
  * For example:
  *
  * {{{
  * withLocalThreadPool(Executors.newThreadPool(3)) {
  *     val f = Future { ... }
  *     val g = f.map(...)
  *     g.filter(...)
  * }
  * }}}
  *
  * This construct waits for the future produced by the code to terminate, shuts down the execution
  * context using `shutdown` and waits for the context to terminate. If interrupted while waiting,
  * method `shutdownNow` is called on the execution context.
  *
  * @since 1.0
  */
def withLocalThreadPool[A, Exec <: ExecutorService](exec: Exec)(code: Exec ?=> Future[A]): A =
   try
      val result = Try(Await.result(code(using exec), Duration.Inf))
      exec.shutdownAndWait(Double.PositiveInfinity)
      result.get
   catch
      case ex: InterruptedException =>
         exec.shutdownNow()
         throw ex

def withLocalThreads[A](maxThreads: Int = 0)(code: ExecutionContextExecutorService ?=> Future[A]): A =
   require(maxThreads >= 0, s"maxThreads cannot be negative, is $maxThreads")
   val exec =
      if maxThreads > 0 then Executors.newThreadPool(maxThreads)
      else Executors.newUnlimitedThreadPool()
   withLocalThreadPool(exec)(code)

/** A simplified variant of `withLocalThreadPool`.
  *
  * The differences are:
  *   - an unlimited thread pool is created by the function;
  *   - `code` is not required to return a future.
  *
  * If `waitForTermination` is true, the behavior is that of `withLocalThreadPool`. Otherwise, the
  * thread pool is not shut down, and its idle threads terminate after 1 second.
  *
  * @since 1.0
  */
def withUnlimitedThreads[U](waitForTermination: Boolean = false)(
    code: ExecutionContextExecutorService => U
): Unit =
   val exec = Executors.newUnlimitedThreadPool()
   if waitForTermination then withLocalThreadPool(exec)(Future.successful(code(exec)))
   else code(exec)

/** A simplified variant of `withLocalThreadPool`.
  *
  * This is the short form of `withUnlimitedThreads` that uses the default value:
  * `withUnlimitedThreads {...}` is equivalent to `withUnlimitedThreads(false) {...}`.
  *
  * @since 1.0
  */
def withUnlimitedThreads[U](code: ExecutionContextExecutorService => U): Unit =
   withUnlimitedThreads()(code)
