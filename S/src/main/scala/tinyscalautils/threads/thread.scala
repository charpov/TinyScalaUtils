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
)(
    code: => U
): Thread = newFactoryThread(name, start, daemon, Thread(_), code)

/** A Kotlin-like function to create stoppable threads.
  *
  * Empty names are ignored.
  *
  * @since 1.0
  */
def newStoppableThread[U](
    delay: Double = 1.0,
    name: String = "",
    start: Boolean = true,
    daemon: Boolean = false,
    logging: Boolean = true
)(
    code: => U
): Thread =
   newFactoryThread(name, start, daemon, StoppableThread(_, delay, logging), code)

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
