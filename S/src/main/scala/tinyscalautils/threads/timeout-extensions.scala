package tinyscalautils.threads

import tinyscalautils.timing.toNanos

import java.util.concurrent.*
import scala.concurrent.duration.NANOSECONDS

extension (exec: ExecutorService)
   /** Shuts down the executor and waits for termination.
     *
     * If the executor fails to terminate and `force` is set to true, invokes `shutdownNow`.
     *
     * @param seconds
     *   timeout, in seconds.
     * @param force
     *   if true, `shutdownNow` is invoked after a timeout.
     * @return
     *   true if the executor terminates before the timeout.
     * @since 1.0
     */
   @throws[InterruptedException]
   def shutdownAndWait(seconds: Double = Double.PositiveInfinity, force: Boolean = false): Boolean =
      exec.shutdown()
      exec.awaitTermination(seconds.toNanos, NANOSECONDS) || {
         if force then exec.shutdownNow()
         false
      }

   /** Floating seconds version of `awaitTermination`.
     *
     * @param seconds
     *   timeout, in seconds.
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
     * @return
     *   true is thread terminates within time limit.
     */
   def joined(seconds: Double): Boolean =
      thread.join((seconds * 1E3).round)
      !thread.isAlive

extension [A](queue: BlockingQueue[A])
   /** Like `offer` but timeout in seconds.
    *
    * @since 1.2
    */
   def offer(value: A, seconds: Double): Boolean = queue.offer(value, seconds.toNanos, NANOSECONDS)

   /** Like `poll` but timeout in seconds and `null` wrapped in an option.
     *
     * @since 1.2
     */
   def pollOption(seconds: Double): Option[A] = Option(queue.poll(seconds.toNanos, NANOSECONDS))
