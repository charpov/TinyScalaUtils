package tinyscalautils.threads

/** Adds a Boolean `joined` method on `Thread` that checks thread status after `join`.
  *
  * @since 1.0
  */
extension (thread: Thread)
   /** Waits for thread termination.
     *
     * @param seconds
     *   timeout, in seconds.
     *
     * @return
     *   true is thread terminates within time limit.
     */
   def joined(seconds: Double): Boolean =
      thread.join((seconds * 1E3).round)
      !thread.isAlive
