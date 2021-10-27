package tinyscalautils.threads

import scala.concurrent.duration.NANOSECONDS
import java.util.concurrent.{ Executors, ScheduledExecutorService }
import scala.concurrent.{ ExecutionContext, Future, Promise }
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.Try

extension [A](future: Future[A])

   /** Adds a orTimeout method similar to Java's
     * [[java.util.concurrent.CompletableFuture#orTimeout]].
     *
     * The new future has the same outcome, unless the computation times out. In that case, it is a
     * failed future with a [[java.util.concurrent.TimeoutException]]. An optional cancellation code
     * can be passed by name.
     *
     * @param seconds
     *   timeout, in seconds.
     *
     * @param cancelCode
     *   arbitrary code to run on timeout; this is run ''after'' the future is failed, on the
     *   execution context (not the timer); defaults to noOp.
     *
     * @param timer
     *   a scheduled executor used to cancel the future after the timeout, and to run the
     *   cancellation code, if any. For convenience, [[timeoutTimer]] can be imported as a timer
     *   available implicitly.
     *
     * @return
     *   a future that completes either with the result of the initial future, or with a
     *   [[java.util.concurrent.TimeoutException]].
     *
     * @since 1.0
     */
   def orTimeout(seconds: Double, cancelCode: => Any = ())(
       using exec: ExecutionContext,
       timer: ScheduledExecutorService
   ): Future[A] =
      // skip mechanics if future already completed or already timed out
      if future.isCompleted then future
      else if seconds <= 0.0 then
         exec.execute(() => cancelCode)
         Future.failed(TimeoutException())
      else
         val promise = Promise[A]()
         val interrupt: Runnable =
            () => if promise.tryFailure(TimeoutException()) then exec.execute(() => cancelCode)
         val timerFuture = timer.schedule(interrupt, (seconds * 1E9).round, NANOSECONDS)
         future.onComplete { result =>
            timerFuture.cancel(false)
            promise.tryComplete(result)
         }
         promise.future
   end orTimeout

   /** Adds a completeOnTimeout method similar to Java's
     * [[java.util.concurrent.CompletableFuture#completeOnTimeout]].
     *
     * The new future has the same outcome, unless the computation times out. In that case, the
     * future is completed by executing the fallback code, passed by name. The fallback code runs in
     * the execution context (not the timer).
     *
     * The `strict` flags controls the race between timeout and normal completion. If false, the
     * race is between normal completion and ''termination'' of the fallback code. In other words,
     * the fallback computation is initiated at the timeout, but the original computation can still
     * complete while the fallback code is running (in which case the fallback value is computed but
     * not used).
     *
     * If true, the race is between normal completion and ''initiation'' of the fallback code. Once
     * the fallback code is started, its value will be used to complete the future, even if normal
     * completion of the initial task occurs in the meantime.
     *
     * @param fallbackCode
     *   code to produce a fallback value in case of timeout.
     *
     * @param seconds
     *   timeout, in seconds.
     *
     * @param timer
     *   a scheduled executor used to complete the future after the timeout. For convenience,
     *   [[timeoutTimer]] can be imported as a timer available implicitly.
     *
     * @return
     *   a future that completes either with the result of the initial future, or with the result of
     *   the given computation in case of timeout
     *
     * @since 1.0
     */
   def completeOnTimeout(seconds: Double, strict: Boolean = false)(fallbackCode: => A)(
       using exec: ExecutionContext,
       timer: ScheduledExecutorService
   ): Future[A] =
      if strict then completeOnTimeoutStrict(seconds, fallbackCode)
      else completeOnTimeoutLoose(seconds, fallbackCode)

   private def completeOnTimeoutLoose(seconds: Double, fallbackCode: => A)(
       using exec: ExecutionContext,
       timer: ScheduledExecutorService
   ): Future[A] =
      // skip mechanics if future already completed
      if future.isCompleted then future
      else
         val promise             = Promise[A]()
         val interrupt: Runnable = () => promise.completeWith(Future(fallbackCode))
         val timerFuture         = timer.schedule(interrupt, (seconds * 1E9).round, NANOSECONDS)

         future.onComplete { result =>
            timerFuture.cancel(false)
            promise.tryComplete(result)
         }
         promise.future
   end completeOnTimeoutLoose

   private def completeOnTimeoutStrict(seconds: Double, fallbackCode: => A)(
       using exec: ExecutionContext,
       timer: ScheduledExecutorService
   ): Future[A] =
      // skip mechanics if future already completed or already timed out
      if future.isCompleted then future
      else if seconds <= 0.0 then Future(fallbackCode)
      else
         val shouldComplete = AtomicBoolean(true)
         val promise        = Promise[A]()
         val interrupt: Runnable =
            () => if shouldComplete.getAndSet(false) then promise.completeWith(Future(fallbackCode))
         val timerFuture = timer.schedule(interrupt, (seconds * 1E9).round, NANOSECONDS)

         future.onComplete { result =>
            timerFuture.cancel(false)
            if shouldComplete.getAndSet(false) then promise.tryComplete(result)
         }
         promise.future
   end completeOnTimeoutStrict

/** A single-thread timer.
  *
  * The thread is created in daemon mode.
  *
  * @since 1.0
  */
given timeoutTimer: ScheduledExecutorService =
   def newThread(r: Runnable): Thread =
      val thread = Executors.defaultThreadFactory().newThread(r)
      thread.setDaemon(true)
      thread.setName("timeoutTimer")
      thread
   Executors.newSingleThreadScheduledExecutor(newThread(_))
