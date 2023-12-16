package tinyscalautils.threads

import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit.NANOSECONDS
import tinyscalautils.timing.toNanos

import scala.annotation.experimental

extension [A](queue: BlockingQueue[A])
   @experimental
   def offer(value: A, seconds: Double): Boolean = queue.offer(value, seconds.toNanos, NANOSECONDS)

   @experimental
   def pollOption(seconds: Double): Option[A] = Option(queue.poll(seconds.toNanos, NANOSECONDS))

