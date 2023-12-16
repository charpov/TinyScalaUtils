package tinyscalautils.threads

import tinyscalautils.timing.toNanos

import java.util.concurrent.Semaphore
import scala.concurrent.duration.NANOSECONDS

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
