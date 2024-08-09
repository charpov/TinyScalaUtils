package tinyscalautils.threads

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/** A factory of marked threads.
  *
  * The threads produced by this factory have the [[MarkedThread]] marker and are named
  * `MarkedThread-<count>`.
  *
  * @since 1.0
  */
class MarkedThreadFactory extends ThreadFactory:
   private val IDS = AtomicInteger()

   /** Creates a new thread. The thread has type `MarkedThread` and is not started.
     *
     * Note: the return type of the method is `Thread`, not `MarkedThread`, so that thread factory
     * mixins (`KeepThreads`, `LimitedThreads`, etc.) can be used.
     */
   def newThread(r: Runnable): Thread =
      new Thread(r, "MarkedThread-" + IDS.incrementAndGet()) with MarkedThread

/** A default marked thread factory. */
object MarkedThreadFactory extends MarkedThreadFactory
