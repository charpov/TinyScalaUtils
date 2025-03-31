package tinyscalautils.threads

import java.util.concurrent.ExecutorService
import scala.compiletime.summonInline
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContextExecutorService, Future }

/** Runs code within an implicit execution context.
  *
  * If the code produces a future, this construct waits for completion of this future and returns
  * its value. Otherwise, it returns _unit_.
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
transparent inline def withThreads[A, Exec](executor: Exec, inline shutdown: Boolean)(
    code: Exec ?=> A
) =
   val f = exec => code(using exec)
   inline f match
      case g: (Exec => Future[?]) =>
         _withThreadPoolAndWait(executor, shutdown)(g)
      case _ =>
         _withThreadPoolAndWait(executor, shutdown)(exec => { f(exec); Future.unit })

/** Runs code within an implicit execution context.
  *
  * This is equivalent to `withThreads(exec, shutdown = false)(code)`.
  *
  * @since 1.6
  */
@throws[InterruptedException]
transparent inline def withThreads[A, Exec](exec: Exec)(code: Exec ?=> A): Any =
   withThreads(exec, shutdown = false)(code)

/** Runs code within a newly created implicit thread pool.
  *
  * If the code produces a future, this construct waits for completion of this future and returns
  * its value. Otherwise, it returns _unit_.
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
transparent inline def withThreads[A]()(code: ExecutionContextExecutorService ?=> A): Any =
   _withThreads(0, awaitTermination = false)(code)

transparent inline def _withThreads[A](n: Int, awaitTermination: Boolean)(
    code: ExecutionContextExecutorService ?=> A
) =
   val f = exec => code(using exec)
   inline f match
      case g: (ExecutionContextExecutorService => Future[?]) =>
         _withThreadsAndWait(n, awaitTermination)(g)
      case _ =>
         _withThreadsAndWait(n, awaitTermination)(exec => { f(exec); Future.unit })

def _withThreadsAndWait[A](maxThreads: Int, awaitTermination: Boolean = false)(
    code: ExecutionContextExecutorService => Future[A]
): A =
   val exec =
      if maxThreads == 0 then Executors.newUnlimitedThreadPool()
      else Executors.newThreadPool(maxThreads)
   val result = _withThreadPoolAndWait(exec, shutdown = true)(code)
   if awaitTermination then exec.awaitTermination()
   result

inline def _withThreadPoolAndWait[A, Exec](exec: Exec, inline shutdown: Boolean = false)(
    code: Exec => Future[A]
): A =
   try Await.result(code(exec), Duration.Inf)
   finally if shutdown then summonInline[Exec <:< ExecutorService](exec).shutdown()

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
): Unit = withUnlimitedThreadsAndWait(awaitTermination):
   code
   Future.unit

@deprecated("use withThreads instead", since = "1.6")
def withUnlimitedThreadsAndWait[A](code: ExecutionContextExecutorService ?=> Future[A]): A =
   withUnlimitedThreadsAndWait()(code)

@deprecated("use withThreads instead", since = "1.6")
def withUnlimitedThreads[U](code: ExecutionContextExecutorService ?=> U): Unit =
   withUnlimitedThreads()(code)
