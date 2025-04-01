package tinyscalautils.threads

import tinyscalautils.timing.{ getTime, toNanos }

import java.util.concurrent.*
import scala.concurrent.duration.NANOSECONDS

extension (exec: ExecutorService)
   /** Shuts down the executor and waits for termination.
     *
     * If the executor fails to terminate (before a timeout or an interrupt) and `force` is set to true, invokes `shutdownNow`.
     *
     * @param seconds
     *   timeout, in seconds.
     *
     * @param force
     *   if true, `shutdownNow` is invoked after a timeout or an interrupt.
     *
     * @return
     *   true if the executor terminates before the timeout.
     *
     * @since 1.0
     */
   @throws[InterruptedException]
   def shutdownAndWait(seconds: Double, force: Boolean = false): Boolean =
      exec.shutdown()
      try
         exec.awaitTermination(seconds.toNanos, NANOSECONDS) || {
            if force then exec.shutdownNow()
            false
         }
      catch case ex: InterruptedException =>
         if force then exec.shutdownNow()
         throw ex

   /** Shuts down the executor and waits forever for termination.
     *
     * @return
     *   true.
     *
     * @since 1.0
     */
   @throws[InterruptedException]
   def shutdownAndWait(): Boolean = shutdownAndWait(force = false)

   /** Shuts down the executor and waits forever for termination.
     *
     * @return
     *   true.
     *
     * @param force
     *   if true, `shutdownNow` is invoked after a timeout.
     *
     * @since 1.7
     */
   @throws[InterruptedException]
   def shutdownAndWait(force: Boolean): Boolean =
      shutdownAndWait(Double.PositiveInfinity, force = force)

   /** Floating seconds version of `awaitTermination`.
     *
     * @param seconds
     *   timeout, in seconds.
     *
     * @see
     *   [[java.util.concurrent.ExecutorService#awaitTermination]]
     * @since 1.0
     */
   @throws[InterruptedException]
   def awaitTermination(seconds: Double = Double.PositiveInfinity): Boolean =
      exec.awaitTermination(seconds.toNanos, NANOSECONDS)

extension (latch: CountDownLatch)
   /** A single `countdown`, followed by `await`. */
   def countDownAndWait(seconds: Double): Boolean =
      latch.countDown()
      latch.await(seconds)

   /** A single `countdown`, followed by `await`. */
   def countDownAndWait(): Unit =
      latch.countDown()
      latch.await()

   /** Same as standard `await` but in seconds.
     *
     * @since 1.1
     */
   def await(seconds: Double): Boolean = latch.await(seconds.toNanos, NANOSECONDS)

extension (barrier: CyclicBarrier)
   /** Same as standard `await` but in seconds.
     *
     * @since 1.2
     */
   def await(seconds: Double): Int = barrier.await(seconds.toNanos, NANOSECONDS)

extension (sem: Semaphore)
   /** Same as standard `tryAcquire` but in seconds.
     *
     * @note
     *   Should be named `tryAcquire`, but that conflicts with existing signatures.
     *
     * @since 1.1
     */
   def acquire(permits: Int, seconds: Double): Boolean =
      sem.tryAcquire(permits, seconds.toNanos, NANOSECONDS)

extension (thread: Thread)
   /** Waits for thread termination.
     *
     * @param seconds
     *   timeout, in seconds.
     *
     * @param start
     *   A starting point for wait time, as per [[getTime]].
     *
     * @return
     *   true is thread terminates within time limit.
     */
   def joined(seconds: Double, start: Long = getTime()): Boolean =
      NANOSECONDS.timedJoin(thread, seconds.toNanos + start - getTime())
      !thread.isAlive

   /** Same as `isSpinning(seconds = 1.0)`. */
   def isSpinning: Boolean = isSpinning(seconds = 1.0)

   /** Same as `isSpinning(seconds, threshold = 0.01)`. */
   def isSpinning(seconds: Double): Boolean = isSpinning(seconds, threshold = 0.01)

   /** Checks if a thread is spinning.
     *
     * This method works by calculating how much CPU time a thread is using during a span of time.
     * It then returns true if this time is above a specified threshold. If the thread is not alive
     * when the method starts or terminates while measuring CPU, the method returns false. The
     * method also returns false for virtual threads.
     *
     * @param seconds
     *   The span of time used to measure CPU activity; must be positive; defaults to 1 second.
     *
     * @param threshold
     *   The threshold of activity to reach to be considered spinning; must be between 0 and 1.
     *
     * @throws UnsupportedOperationException
     *   if measuring CPU time is not supported by the platform.
     */
   def isSpinning(seconds: Double = 1.0, threshold: Double): Boolean =
      val startTime = getTime()
      if !canCheckSpinning then
         throw UnsupportedOperationException("spinning check is not supported on this JVM")
      require(
        threshold > 0.0 && threshold < 1.0,
        s"threshold must be in the range (0,1), not $threshold"
      )
      require(seconds > 0.0, s"seconds must be positive, not $seconds")

      // replace getId with threadID in Java >= 19
      def cpu() = threadInfo.getThreadCpuTime(thread.getId)

      val start = cpu()
      if start == -1 || thread.joined(seconds, startTime) then false
      else
         val end = cpu()
         end != -1 && (end - start) / seconds / 1E9 > threshold

private lazy val threadInfo = management.ManagementFactory.getThreadMXBean
private lazy val canCheckSpinning =
   try
      threadInfo.isThreadCpuTimeEnabled || {
         threadInfo.setThreadCpuTimeEnabled(true)
         threadInfo.isThreadCpuTimeEnabled
      }
   catch case _: UnsupportedOperationException => false

extension [A](queue: BlockingQueue[A])
   /** Like `offer` but timeout in seconds.
     *
     * @since 1.2
     */
   def offer(value: A, seconds: Double): Boolean = queue.offer(value, seconds.toNanos, NANOSECONDS)

   @deprecated("use Option instead", since = "1.3")
   def pollOption(seconds: Double): Option[A] = Option(queue.poll(seconds.toNanos, NANOSECONDS))

   /** Like `poll` but timeout in seconds.
     *
     * @since 1.3
     */
   def poll(seconds: Double): A = queue.poll(seconds.toNanos, NANOSECONDS)
