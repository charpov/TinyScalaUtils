package tinyscalautils.threads

import tinyscalautils.timing.toNanos

import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.ExecutionContext.parasitic
import scala.concurrent.{ ExecutionContext, Future, Promise }
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
     *   a timer used to cancel the future after the timeout, and to run the cancellation code, if
     *   any. For convenience, [[timeoutTimer]] can be imported as a timer available implicitly.
     *
     * @return
     *   a future that completes either with the result of the initial future, or with a
     *   [[java.util.concurrent.TimeoutException]].
     *
     * @since 1.0
     */
   def orTimeout(seconds: Double, cancelCode: => Any = ())(
       using exec: ExecutionContext,
       timer: Timer
   ): Future[A] =
      // skip mechanics if future already completed or already timed out
      if future.isCompleted then future
      else if seconds <= 0.0 then
         exec.execute(() => cancelCode)
         Future.failed(TimeoutException())
      else
         val promise = Promise[A]()
         timer.schedule(seconds) {
            if promise.tryFailure(TimeoutException()) then exec.execute(() => cancelCode)
         }
         future.onComplete(promise.tryComplete)(using parasitic)
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
     *   a timer used to complete the future after the timeout. For convenience, [[timeoutTimer]]
     *   can be imported as a timer available implicitly.
     *
     * @return
     *   a future that completes either with the result of the initial future, or with the result of
     *   the given computation in case of timeout
     *
     * @since 1.0
     */
   def completeOnTimeout(seconds: Double, strict: Boolean = false)(fallbackCode: => A)(
       using exec: ExecutionContext,
       timer: Timer
   ): Future[A] =
      if strict then completeOnTimeoutStrict(seconds, fallbackCode)
      else completeOnTimeoutLoose(seconds, fallbackCode)

   private def completeOnTimeoutLoose(seconds: Double, fallbackCode: => A)(
       using exec: ExecutionContext,
       timer: Timer
   ): Future[A] =
      // skip mechanics if future already completed
      if future.isCompleted then future
      else
         val promise = Promise[A]()
         timer.schedule(seconds)(promise.completeWith(Future(fallbackCode)))
         future.onComplete(promise.tryComplete)(using parasitic)
         promise.future
   end completeOnTimeoutLoose

   private def completeOnTimeoutStrict(seconds: Double, fallbackCode: => A)(
       using exec: ExecutionContext,
       timer: Timer
   ): Future[A] =
      // skip mechanics if future already completed or already timed out
      if future.isCompleted then future
      else if seconds <= 0.0 then Future(fallbackCode)
      else
         val shouldComplete = AtomicBoolean(true)
         val promise        = Promise[A]()
         timer.schedule(seconds) {
            if shouldComplete.getAndSet(false) then promise.completeWith(Future(fallbackCode))
         }
         future.onComplete { result =>
            if shouldComplete.getAndSet(false) then promise.tryComplete(result)
         }(using parasitic)
         promise.future
   end completeOnTimeoutStrict

/** A single-thread timer.
  *
  * The thread is created in daemon mode.
  *
  * @since 1.0
  */
given timeoutTimer: Timer =
   def newThread(r: Runnable): Thread =
      val thread = java.util.concurrent.Executors.defaultThreadFactory().newThread(r)
      thread.setDaemon(true)
      thread.setName("timeoutTimer")
      thread
   Executors.withFactory(newThread(_)).newTimer(1)
