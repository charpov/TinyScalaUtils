package tinyscalautils.control

import tinyscalautils.threads.newStoppableThread

import scala.util.{ Failure, Try }

/** Executes the given code in a stoppable thread.
  *
  * The calling thread waits for the stoppable thread to terminate. If the calling thread is
  * interrupted, the stoppable thread is interrupted, and the interrupted exception is thrown. After
  * the given delay, the stoppable thread is stopped if it is still running.
  *
  * Note that `delay` is not a timeout; a thread that calls `stoppably` can be blocked indefinitely.
  *
  * @param delay
  *   time (in seconds) given to a task running thread to terminate after being interrupted, and
  *   before being forcibly stopped.
  *
  * @throws InterruptedException
  *   if the calling thread is interrupted while the stoppable thread is running.
  *
  * @throws java.lang.IllegalArgumentException
  *   if the delay is negative.
  *
  * @since 1.0
  */
@throws[InterruptedException]
@throws[IllegalArgumentException]("if delay is negative")
def stoppably[A](delay: Double = 1.0)(code: => A): A =
   var out: Try[A] = Failure(Error("fatal"))
   val runner = newStoppableThread(delay) {
      out = Try(code) // 'join' is used for memory barrier
   }
   try runner.join()
   catch
      case e: InterruptedException =>
         runner.interrupt()
         throw e
   out.get

/** Executes the given code in a stoppable thread.
  *
  * This is the shorter version of `stoppably` that uses a default delay.
  *
  * @since 1.0
  */
@throws[InterruptedException]
@throws[IllegalArgumentException]("if delay is negative")
def stoppably[A](code: => A): A = stoppably()(code)
