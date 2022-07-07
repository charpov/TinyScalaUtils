package tinyscalautils.threads

import net.jcip.annotations.Immutable
import tinyscalautils.timing.toNanos
import tinyscalautils.assertions.require
import java.util
import java.util.concurrent.*
import java.util.logging.Logger
import scala.concurrent.duration.{ Duration, NANOSECONDS, SECONDS }
import scala.concurrent.{ Await, ExecutionContext, ExecutionContextExecutorService, Future }
import scala.util.control.NonFatal

/** A factory for customized thread pools.
  *
  * It makes it easier to specify a rejected execution handler (including `DiscardPolicy`) and/or a
  * thread factory (including `defaultThreadFactory`). Furthermore, cached thread pools are created
  * with a keepalive time of 1 second instead of 1 minute, which is useful in tests and demos that
  * cannot conveniently shut down a thread pool. Finally, thread pools are returned as instances of
  * `ExecutionContextExecutorService`, which makes them suitable for use with Java or Scala
  * constructs.
  *
  * Instances of this class are created through the companion object, e.g.:
  *
  * {{{
  * val f1 = Executors.silent
  * val f2 = Executors.silent.withFactory(tf)
  * }}}
  *
  * @since 1.0
  */
@Immutable
class Executors private (tf: Option[ThreadFactory], rej: Option[RejectedExecutionHandler]):

   /** Creates a new execution context as a fixed thread pool.
     *
     * Uses the rejected execution handler and thread factory of the current instance.
     *
     * @param size
     *   the number of threads in the pool; must be positive.
     *
     * @since 1.0
     */
   @throws[IllegalArgumentException]("if the specified size is not positive")
   def newThreadPool(size: Int): ExecutionContextExecutorService =
      require(size > 0, s"thread pool size must be positive, not $size")
      ExecutionContext.fromExecutorService {
         ThreadPoolExecutor(
           size,
           size,
           0L,
           NANOSECONDS,
           LinkedBlockingQueue[Runnable](),
           tf.getOrElse(java.util.concurrent.Executors.defaultThreadFactory()),
           rej.getOrElse(ThreadPoolExecutor.AbortPolicy())
         )
      }

   /** Creates a new execution context as an unlimited thread pool.
     *
     * Uses the rejected execution handler and thread factory of the current instance.
     *
     * @param keepAlive
     *   time to keep idle threads alive, in seconds.
     *
     * @since 1.0
     */
   @throws[IllegalArgumentException]("if the specified keepalive time is not positive")
   def newUnlimitedThreadPool(keepAlive: Double = 1.0): ExecutionContextExecutorService =
      require(keepAlive >= 0.0, s"keep alive time must be nonnegative, not $keepAlive")
      ExecutionContext.fromExecutorService {
         ThreadPoolExecutor(
           0,
           Integer.MAX_VALUE,
           keepAlive.toNanos,
           NANOSECONDS,
           SynchronousQueue(),
           tf.getOrElse(java.util.concurrent.Executors.defaultThreadFactory()),
           rej.getOrElse(ThreadPoolExecutor.AbortPolicy())
         )
      }

   /** Creates a new execution context with timer facilities, as a fixed thread pool.
     *
     * Uses the rejected execution handler and thread factory of the current instance.
     *
     * @param size
     *   the number of threads in the pool; must be positive.
     *
     * @since 1.0
     */
   @throws[IllegalArgumentException]("if the specified size is not positive")
   def newTimer(size: Int): ExecutionContextExecutorService & Timer =
      require(size > 0, s"thread pool size must be positive, not $size")
      TimerPool(
        size,
        tf.getOrElse(java.util.concurrent.Executors.defaultThreadFactory()),
        rej.getOrElse(ThreadPoolExecutor.AbortPolicy())
      )

   /** Returns a thread pool factory that uses a `DiscardPolicy` rejected execution handler, and the
     * same thread factory as before.
     *
     * @see
     *   [[java.util.concurrent.ThreadPoolExecutor.DiscardPolicy]]
     *
     * @since 1.0
     */
   def silent: Executors = Executors(tf, Some(ThreadPoolExecutor.DiscardPolicy()))

   /** Returns a thread pool factory that uses the given thread factory, and the same rejected
     * execution handler as before..
     *
     * @since 1.0
     */
   def withFactory(tf: ThreadFactory): Executors = Executors(Some(tf), rej)

   /** Returns a thread pool factory that uses the given rejected execution handler, and the same
     * thread factory as before.
     *
     * @since 1.0
     */
   def withRejectionPolicy(rej: RejectedExecutionHandler): Executors = Executors(tf, Some(rej))

/** Companion object.
  *
  * It is itself an instance of `Executors` that uses the default rejected execution handler and
  * thread factory.
  *
  * @since 1.0
  */
object Executors extends Executors(None, None):
   /** Implicit thread pool.
     *
     * It is an unlimited thread pool with the default rejected execution handler, thread factory
     * and keepalive time (1 second).
     *
     * @since 1.0
     */
   given global: ExecutionContextExecutorService = ExecutionContext.fromExecutorService {
      new ThreadPoolExecutor(
        0,
        Integer.MAX_VALUE,
        1L,
        SECONDS,
        SynchronousQueue()
      ):
         override def shutdown(): Unit =
            try super.shutdown()
            finally Logger.getLogger("tinyscalautils").warning("global thread pool shut down")

         override def shutdownNow(): util.List[Runnable] =
            try super.shutdownNow()
            finally Logger.getLogger("tinyscalautils").warning("global thread pool shut down (now)")
   }

extension (exec: Executor)
   /** Allows a by-name argument to replace an explicit `Runnable`.
     *
     * Instead of `exec.execute(() => code)`, you can write `exec.run(code)`.
     */
   def run[U](code: => U): Unit = exec.execute(() => code)

object Execute:
   /** Like `Future {..}`, but does not construct a future. */
   def apply[U](code: => U)(using exec: Executor): Unit = exec.run(code)
