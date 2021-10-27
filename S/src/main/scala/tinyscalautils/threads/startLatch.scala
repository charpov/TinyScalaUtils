package tinyscalautils.threads

import java.util.concurrent.CountDownLatch
import scala.concurrent.duration.NANOSECONDS
import tinyscalautils.timing.toNanos

extension (latch: CountDownLatch)
   /** A single `countdown`, followed by `await`. */
   def countDownAndWait(seconds: Double = 1.0): Boolean =
      latch.countDown()
      latch.await(seconds.toNanos, NANOSECONDS)
