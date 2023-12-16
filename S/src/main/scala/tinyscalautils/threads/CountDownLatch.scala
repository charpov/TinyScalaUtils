package tinyscalautils.threads

import java.util.concurrent.CountDownLatch
import scala.concurrent.duration.NANOSECONDS
import tinyscalautils.timing.toNanos

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
