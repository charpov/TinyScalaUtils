package tinyscalautils.threads

/** A marker trait for test threads.
  *
  * This can be used as a cheap alternative to [[KeepThreadsFactory]] to keep track of threads
  * created by a factory, as a group.
  *
  * @see
  *   [[isMarkedThread]]
  *
  * @since 1.0
  */
trait MarkedThread extends Thread

extension (thread: Thread)
   /** Adds an `isMarkedThread` method to threads.
     *
     * @see
     *   [[MarkedThread]]
     *
     * @since 1.0
     */
   def isMarkedThread: Boolean = thread.isInstanceOf[MarkedThread]
