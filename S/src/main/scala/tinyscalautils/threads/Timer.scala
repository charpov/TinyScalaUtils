package tinyscalautils.threads

import tinyscalautils.timing.toNanos

import java.util.concurrent.*
import scala.concurrent.duration.NANOSECONDS
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future, Promise}
import scala.util.Try

/** Simple timers.
  *
  * @since 1.0
  */
trait Timer:
   /** Schedules a task for delayed execution. */
   def schedule[A](delay: Double)(code: => A): Future[A]

   /** Schedules a task for repeated execution.
     *
     * Note that there is no mechanism to cancel the task, short of shutting down the timer
     */
   def scheduleAtFixedRate[U](initDelay: Double, rate: Double)(code: => U): Unit

   /** Schedules a task for repeated execution.
     *
     * Note that there is no mechanism to cancel the task, short of shutting down the timer
     */
   def scheduleWithFixedDelay[U](initDelay: Double, delay: Double)(code: => U): Unit

   /** Shuts down the timer.
     *
     * Delayed tasks will still run, but repeated tasks are stopped.
     */
   def shutdown(): Unit

private class TimerPool(size: Int, tf: ThreadFactory, rej: RejectedExecutionHandler)
    extends ExecutionContextExecutorService
    with Timer:

   private val exec = ScheduledThreadPoolExecutor(size, tf, rej)

   def reportFailure(cause: Throwable): Unit = ExecutionContext.defaultReporter(cause)

   def schedule[A](delay: Double)(code: => A): Future[A] =
      val p              = Promise[A]()
      val task: Runnable = () => p.complete(Try(code))
      exec.schedule(task, delay.toNanos, NANOSECONDS)
      p.future

   def scheduleAtFixedRate[U](initDelay: Double, rate: Double)(code: => U): Unit =
      val task: Runnable = () => code
      exec.scheduleAtFixedRate(task, initDelay.toNanos, rate.toNanos, NANOSECONDS)

   def scheduleWithFixedDelay[U](initDelay: Double, delay: Double)(code: => U): Unit =
      val task: Runnable = () => code
      exec.scheduleWithFixedDelay(task, initDelay.toNanos, delay.toNanos, NANOSECONDS)

   export exec.{
      execute,
      submit,
      invokeAll,
      invokeAny,
      shutdown,
      shutdownNow,
      awaitTermination,
      isShutdown,
      isTerminated
   }

/** Implicit use of timers. */
object DelayedFuture:
   /** Schedules a delayed task.
     *
     * This is equivalent to `timer.schedule(delay)(code)` on the implicit timer.
     *
     * @since 1.0
     */
   def apply[A](delay: Double)(code: => A)(using timer: Timer): Future[A] =
      timer.schedule(delay)(code)
