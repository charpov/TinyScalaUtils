package tinyscalautils.threads

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger
import scala.annotation.nowarn
import scala.concurrent.duration.NANOSECONDS
import tinyscalautils.timing.toNanos

/** A stoppable thread.
  *
  * These threads are forcefully ``stopped`` when not responding to interrupts.
  *
  * If the thread is still alive `delay` seconds after having been interrupted, method `stop` is
  * invoked. Delay value cannot be negative.
  *
  * Unless logging is turned off, killing is logged at `INFO` level in the `tinyscalautils.threads`
  * logger.
  *
  * Note that this class uses method `Thread.stop`, which has been deprecated since Java 2.
  *
  * @since 1.0
  */
@deprecated("Uses Thread.stop, which is deprecated. No replacement.", "1.0")
class StoppableThread @throws[IllegalArgumentException]("if delay is negative") (
    task: Runnable,
    delay: Double = 1.0,
    logging: Boolean = true
)(using timer: ScheduledExecutorService)
    extends Thread(task):

   require(delay >= 0.0)

   /** Interrupts the thread and schedule later killing if necessary. */
   override def interrupt(): Unit =
      super.interrupt()
      val kill: Runnable = () =>
         if isAlive then
            if logging then StoppableThread.logger.info(s"forcibly stopping thread $getName")
            stop(): @nowarn
      timer.schedule(kill, delay.toNanos, NANOSECONDS)

private object StoppableThread:
   lazy private val logger = Logger.getLogger("tinyscalautils.threads")
