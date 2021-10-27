package tinyscalautils.threads

import java.util.concurrent.ExecutorService
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.{ Duration, NANOSECONDS }
import scala.util.control.NonFatal

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
  * withLocalContext(Executors.newThreadPool(3)) {
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
def withLocalContext[A, Exec <: ExecutorService](exec: Exec)(code: Exec ?=> Future[A]): A =
   try
      try
         val out =
            try Await.result(code(using exec), Duration.Inf)
            finally exec.shutdown()
         exec.awaitTermination(Long.MaxValue, NANOSECONDS)
         out
      catch
         case NonFatal(e1) =>
            exec.awaitTermination(Long.MaxValue, NANOSECONDS)
            throw e1
   catch
      case e2: InterruptedException =>
         exec.shutdownNow()
         throw e2
