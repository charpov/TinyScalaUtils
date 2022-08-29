package tinyscalautils.threads

import java.util.concurrent.ExecutorService
import scala.concurrent.duration.NANOSECONDS
import tinyscalautils.timing.toNanos

/** Adds a `shutdownAndWait` method to executor services.
  *
  * @since 1.0
  */
extension (exec: ExecutorService)
   /** Shuts down the executor and waits for termination.
     *
     * If the executor fails to terminate and `force` is set to true, invokes `shutdownNow`.
     *
     * @param seconds
     *   timeout, in seconds.
     *
     * @param force
     *   if true, `shutdownNow` is invoked after a timeout.
     *
     * @return
     *   true if the executor terminates before the timeout.
     *
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
     *
     * @see
     *   [[java.util.concurrent.ExecutorService#awaitTermination]]
     *
     * @since 1.0
     */
   @throws[InterruptedException]
   def awaitTermination(seconds: Double = Double.PositiveInfinity): Boolean =
      exec.awaitTermination(seconds.toNanos, NANOSECONDS)
