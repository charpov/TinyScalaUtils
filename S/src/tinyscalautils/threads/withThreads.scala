package tinyscalautils.threads

import java.util.concurrent.ExecutorService
import scala.compiletime.summonInline
import scala.concurrent.duration.Duration
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{ Await, Awaitable, ExecutionContextExecutorService, Future }
import tinyscalautils.control.before

/** The given thread pool.
  *
  * For instance:
  * {{{
  *   withThreads(16):
  *     val exec = theThreads
  *     ...
  * }}}
  *
  * @note
  *   This is the same as `summon[ExecutionContextExecutorService]`.
  *
  * @since 1.7
  */
def theThreads(using exec: ExecutionContextExecutorService): ExecutionContextExecutorService = exec

/** Runs code within an implicit execution context.
  *
  * If the code produces a future (or more generally, an `Awaitable`), this construct waits for
  * completion of this future and returns its value. Otherwise, it returns the value of the code
  * itself.
  *
  * For example:
  *
  * {{{
  * val result = withThreads(myContext, shutdown = true):
  *    val f = Future { ... }
  *    val g = f.map(...)
  *    g.filter(...)
  * }}}
  *
  * If `shutdown` is true, the execution context must be a subtype of `ExecutorService` and is
  * shutdown, but not waited on.
  *
  * @since 1.6
  */
@throws[InterruptedException]
transparent inline def withThreads[Exec, A](executor: Exec, inline shutdown: Boolean)(
    code: Exec ?=> A
): Any =
   try _run(executor)(exec => code(using exec))
   finally if shutdown then summonInline[Exec <:< ExecutorService](executor).shutdown()

private transparent inline def _run[Exec, A](executor: Exec)(code: Exec => A) =
   inline code match
      case futureCode: Function[Exec, Awaitable[?]] => Await.result(futureCode(executor), Inf)
      case _ => Await.result(Future.successful(code(executor)), Inf)

/** Runs code within an implicit execution context.
  *
  * This is equivalent to `withThreads(executor, shutdown = false)(code)`.
  *
  * @since 1.6
  */
@throws[InterruptedException]
transparent inline def withThreads[Exec, A](executor: Exec)(
    code: Exec ?=> A
): Any = withThreads(executor, shutdown = false)(code)

/** Runs code within a newly created implicit thread pool.
  *
  * If the code produces a future (or more generally, an `Awaitable`), this construct waits for
  * completion of this future and returns its value. Otherwise, it returns the value of the code
  * itself.
  *
  * The thread pool is shutdown and, if `awaitTermination` is true, the code waits for its
  * termination.
  *
  * For example:
  *
  * {{{
  * val result = withThreads(maxThreads = 8, awaitTermination = true):
  *    val f = Future { ... }
  *    val g = f.map(...)
  *    g.filter(...)
  * }}}
  *
  * @throws IllegalArgumentException
  *   if `maxThreads` is not positive.
  *
  * @since 1.6
  */
@throws[InterruptedException]
transparent inline def withThreads[A](maxThreads: Int, awaitTermination: Boolean)(
    code: ExecutionContextExecutorService ?=> A
) =
   require(maxThreads > 0, s"pool size must be positive, not $maxThreads")
   _withThreads(maxThreads, awaitTermination)(code)

/** Runs code within a newly created implicit thread pool.
  *
  * This is equivalent to `withThreads(maxThreads, awaitTermination = false)(code)`.
  *
  * @since 1.6
  */
@throws[InterruptedException]
transparent inline def withThreads[A](
    maxThreads: Int
)(code: ExecutionContextExecutorService ?=> A) =
   require(maxThreads > 0, s"pool size must be positive, not $maxThreads")
   _withThreads(maxThreads, awaitTermination = false)(code)

/** Runs code within a newly created implicit unlimited thread pool.
  *
  * The behavior is the same as [[withThreads(Int,Boolean)]] except that the thread pool is
  * unlimited.
  *
  * @note
  *   This is not quite equivalent to `withThreads(someLargeNumber, awaitTermination)(code)` because
  *   the keepalive time is 1 second on unlimited thread pools but is unlimited on bounded pools.
  *
  * @since 1.6
  */
@throws[InterruptedException]
transparent inline def withThreads[A](awaitTermination: Boolean)(
    code: ExecutionContextExecutorService ?=> A
) = _withThreads(0, awaitTermination)(code)

/** Runs code within a newly created implicit unlimited thread pool.
  *
  * This is equivalent to `withThreads(awaitTermination = false)(code)`.
  *
  * @since 1.6
  */
@throws[InterruptedException]
transparent inline def withThreads[A]()(code: ExecutionContextExecutorService ?=> A) =
   _withThreads(0, awaitTermination = false)(code)

private transparent inline def _withThreads[A](n: Int, awaitTermination: Boolean)(
    code: ExecutionContextExecutorService ?=> A
): Any =
   val executor = if n == 0 then Executors.newUnlimitedThreadPool() else Executors.newThreadPool(n)
   if awaitTermination then
      withThreads(executor, shutdown = true)(code) before executor.awaitTermination()
   else withThreads(executor, shutdown = true)(code)

@deprecated("use withThreads instead", since = "1.6")
inline def withThreadPoolAndWait[A, Exec](exec: Exec, inline shutdown: Boolean = false)(
    code: Exec ?=> Future[A]
): A =
   try Await.result(code(using exec), Duration.Inf)
   finally if shutdown then summonInline[Exec <:< ExecutorService](exec).shutdown()

@deprecated("use withThreads instead", since = "1.6")
def withThreadsAndWait[A](maxThreads: Int, awaitTermination: Boolean = false)(
    code: ExecutionContextExecutorService ?=> Future[A]
): A =
   val exec   = Executors.newThreadPool(maxThreads)
   val result = withThreadPoolAndWait(exec, shutdown = true)(code)
   if awaitTermination then exec.awaitTermination()
   result

@deprecated("use withThreads instead", since = "1.6")
def withUnlimitedThreadsAndWait[A](awaitTermination: Boolean = false)(
    code: ExecutionContextExecutorService ?=> Future[A]
): A =
   val exec   = Executors.newUnlimitedThreadPool()
   val result = withThreadPoolAndWait(exec, shutdown = true)(code)
   if awaitTermination then exec.awaitTermination()
   result

@deprecated("use withThreads instead", since = "1.6")
def withUnlimitedThreads[U](awaitTermination: Boolean = false)(
    code: ExecutionContextExecutorService ?=> U
): Unit =
   withUnlimitedThreadsAndWait(awaitTermination):
      code
      Future.unit

@deprecated("use withThreads instead", since = "1.6")
def withUnlimitedThreadsAndWait[A](code: ExecutionContextExecutorService ?=> Future[A]): A =
   withUnlimitedThreadsAndWait()(code)

@deprecated("use withThreads instead", since = "1.6")
def withUnlimitedThreads[U](code: ExecutionContextExecutorService ?=> U): Unit =
   withUnlimitedThreads()(code)
