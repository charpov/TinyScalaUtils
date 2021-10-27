package tinyscalautils.threads

import tinyscalautils.assertions.require
import tinyscalautils.timing.toNanos

import java.util.concurrent.ExecutorService
import scala.compiletime.summonInline
import scala.concurrent.duration.{ Duration, NANOSECONDS }
import scala.concurrent.{ Await, ExecutionContextExecutorService, Future }
import scala.util.Try

/** Runs code within an implicit execution context, and waits for future completion.
  *
  * For example:
  *
  * {{{
  * withThreadPoolAndWait(myContext) {
  *     val f = Future { ... }
  *     val g = f.map(...)
  *     g.filter(...)
  * }
  * }}}
  *
  * This construct waits for the future produced by the code to terminate. If `shutdown` is true,
  * the execution context must be a subtype of `ExecutorService` and is shut down, but not waited
  * on.
  *
  * @since 1.0
  */
@throws[InterruptedException]
inline def withThreadPoolAndWait[A, Exec](exec: Exec, inline shutdown: Boolean = false)(
    code: Exec ?=> Future[A]
): A =
   try Await.result(code(using exec), Duration.Inf)
   finally if shutdown then summonInline[Exec <:< ExecutorService](exec).shutdown()

/** Runs code within a newly created implicit thread pool, and waits for future completion.
  *
  * For example:
  *
  * {{{
  * withThreadsAndWait(maxThreads = 8) {
  *     val f = Future { ... }
  *     val g = f.map(...)
  *     g.filter(...)
  * }
  * }}}
  *
  * This construct creates a new thread pool and waits for the future produced by the code to
  * terminate. It then shuts down the thread pool and, if `awaitTermination` is true, waits for its
  * termination.
  *
  * @since 1.0
  */
@throws[InterruptedException]
@throws[IllegalArgumentException]("if the specified pool size is not positive")
def withThreadsAndWait[A](maxThreads: Int, awaitTermination: Boolean = false)(
    code: ExecutionContextExecutorService ?=> Future[A]
): A =
   val exec   = Executors.newThreadPool(maxThreads)
   val result = withThreadPoolAndWait(exec, shutdown = true)(code)
   if awaitTermination then exec.awaitTermination()
   result

/** Runs code within a newly created implicit thread pool, and waits for future completion.
  *
  * For example:
  *
  * {{{
  * withUnlimitedThreadsAndWait() {
  *     val f = Future { ... }
  *     val g = f.map(...)
  *     g.filter(...)
  * }
  * }}}
  *
  * This construct creates a new unlimited thread pool and waits for the future produced by the code
  * to terminate. It then shuts down the thread pool and, if `awaitTermination` is true, waits for
  * its termination.
  *
  * @since 1.0
  */
@throws[InterruptedException]
def withUnlimitedThreadsAndWait[A](awaitTermination: Boolean = false)(
    code: ExecutionContextExecutorService ?=> Future[A]
): A =
   val exec   = Executors.newUnlimitedThreadPool()
   val result = withThreadPoolAndWait(exec, shutdown = true)(code)
   if awaitTermination then exec.awaitTermination()
   result

/** Runs code within a newly created implicit thread pool.
  *
  * For example:
  *
  * {{{
  * withThreads(maxThreads = 8) {
  *     Execute { ... }
  *     ...
  * }
  * }}}
  *
  * This construct creates a new thread pool and executes the given code. It then shuts down the
  * thread pool and, if `awaitTermination` is true, waits for its termination.
  *
  * @since 1.0
  */
@throws[InterruptedException]
@throws[IllegalArgumentException]("if the specified pool size is not positive")
def withThreads[U](maxThreads: Int, awaitTermination: Boolean = false)(
    code: ExecutionContextExecutorService ?=> U
): Unit = withThreadsAndWait(maxThreads, awaitTermination) {
   code
   Future.unit
}

/** Runs code within a newly created implicit thread pool.
  *
  * For example:
  *
  * {{{
  * withUnlimitedThreads() {
  *     Execute { ... }
  *     ...
  * }
  * }}}
  *
  * This construct creates a new thread pool and executes the given code. It then shuts down the
  * thread pool and, if `awaitTermination` is true, waits for its termination.
  *
  * @since 1.0
  */
@throws[InterruptedException]
def withUnlimitedThreads[U](awaitTermination: Boolean = false)(
    code: ExecutionContextExecutorService ?=> U
): Unit = withUnlimitedThreadsAndWait(awaitTermination) {
   code
   Future.unit
}

/** A simplified variant of `withUnlimitedThreadsAndWait`.
  *
  * This is the short form of `withUnlimitedThreadsAndWait` that uses the default value:
  * `withUnlimitedThreadsAndWait {...}` is equivalent to `withUnlimitedThreadsAndWait(false) {...}`.
  *
  * @since 1.0
  */
// This method cannot be called because of a bug in the compiler
@throws[InterruptedException]
def withUnlimitedThreadsAndWait[A](code: ExecutionContextExecutorService ?=> Future[A]): A =
   withUnlimitedThreadsAndWait()(code)

/** A simplified variant of `withUnlimitedThreads`.
  *
  * This is the short form of `withUnlimitedThreads` that uses the default value:
  * `withUnlimitedThreads {...}` is equivalent to `withUnlimitedThreads(false) {...}`.
  *
  * @since 1.0
  */
// This method cannot be called because of a bug in the compiler
@throws[InterruptedException]
def withUnlimitedThreads[U](code: ExecutionContextExecutorService ?=> U): Unit =
   withUnlimitedThreads()(code)
