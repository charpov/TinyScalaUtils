package tinyscalautils.threads

import java.util.concurrent.{ ConcurrentLinkedQueue, Executors, ThreadFactory }
import scala.jdk.CollectionConverters.*

/** A thread factory that keeps a reference on all the threads it creates.
  *
  * The factory can be reset: threads created before the reset are discarded.
  *
  * Instances of `KeepThreadsFactory` are thread-safe, and `allThreads` can be invoked while the
  * factory is still being used to create more threads.
  *
  * @see
  *   [[MarkedThreadFactory]]
  *
  * @since 1.0
  */
final class KeepThreadsFactory private (tf: ThreadFactory) extends ThreadFactory:
   private val threads = ConcurrentLinkedQueue[Thread]()

   def newThread(r: Runnable): Thread =
      val thread = tf.newThread(r)
      threads.add(thread)
      thread

   /** All the threads created, in creation order.
     *
     * The sequence only includes all the threads created since the last reset, if any. The
     * collection is live: newly created threads will be added to it.
     *
     * @see
     *   [[resetThreads()]]
     *
     * @since 1.0
     */
   def allThreads: Iterable[Thread] = threads.asScala

   /** Clears the factory.
     *
     * Only the threads created after this reset will be included in the sequence produced by
     * `allThreads`.
     *
     * @see
     *   [[allThreads]]
     *
     * @since 1.0
     */
   def resetThreads(): Unit = threads.clear()

/** Factory methods.
  *
  * @since 1.0
  */
object KeepThreadsFactory:
   /** Creates a thread factory from an existing factory.
     *
     * This factory is thread-safe if the given factory is thread-safe.
     *
     * @since 1.0
     */
   def apply(tf: ThreadFactory) = new KeepThreadsFactory(tf)

   /** Creates a thread factory using `defaultThreadFactory`.
     *
     * @see
     *   [[java.util.concurrent.Executors.defaultThreadFactory]]
     *
     * This factory is thread-safe.
     *
     * @since 1.0
     */
   def apply() = new KeepThreadsFactory(Executors.defaultThreadFactory())
