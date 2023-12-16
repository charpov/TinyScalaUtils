package tinyscalautils.threads

import java.util.concurrent.ThreadFactory

/** A Kotlin-like function to create threads.
  *
  * Empty names are ignored.
  *
  * @since 1.0
  */
def newThread[U](
    name: String = "",
    start: Boolean = true,
    daemon: Boolean = false,
    waitForChildren: Boolean = false
)(
    code: => U
): Thread =
   val task: Runnable = () => code
   val theThread = if waitForChildren then WaitingThread(ThreadGroup(name), task) else Thread(task)
   if name.nonEmpty then theThread.setName(name)
   if daemon then theThread.setDaemon(true)
   if start then theThread.start()
   theThread

/** A Kotlin-like function to create threads.
  *
  * This is the short form of `newThread` that uses default values.
  *
  * @since 1.0
  */
def newThread[U](code: => U): Thread = newThread()(code)

//noinspection ScalaFileName
private class WaitingThread(group: ThreadGroup, task: Runnable) extends Thread(group, task):
   override def run(): Unit =
      try super.run()
      finally
         val threads = Array.ofDim[Thread](16)
         var alive   = group.enumerate(threads)
         while alive > 1 do
            // current thread is most likely threads(0), but this is not guaranteed by the API
            for (i <- 0 until alive) do if threads(i) ne Thread.currentThread then threads(i).join()
            alive = group.enumerate(threads)

private def newFactoryThread[U](
    name: String,
    start: Boolean,
    daemon: Boolean,
    factory: ThreadFactory,
    code: => U
): Thread =
   val theThread = factory.newThread((() => code): Runnable)
   if name.nonEmpty then theThread.setName(name)
   if daemon then theThread.setDaemon(true)
   if start then theThread.start()
   theThread
