package tinyscalautils.collection

import scala.collection.{ AbstractIterator, BuildFrom }
import scala.util.Random

private final class RandomElements[A](sequence: IndexedSeq[A], rand: Random)
    extends AbstractIterator[A]:
   def hasNext: Boolean = true
   def next(): A        = sequence(rand.nextInt(sequence.length))

extension [A](elements: IterableOnce[A])(using rand: Random)
   /** An iterator that produces the elements of a collection in a random (uniform) order.
     *
     * If the collection is empty, the iterator is empty; otherwise, it is infinite.
     *
     * This method iterates over the entire collection and is not thread-safe in general. However,
     * the iterator that is returned relies on its own copy and is thread-safe if the random number
     * generator is thread-safe.
     *
     * @since 1.0
     */
   def randomly: Iterator[A] =
      val sequence = IndexedSeq.from(elements)
      if sequence.isEmpty then Iterator.empty else RandomElements(sequence, rand)

   /** A convenient way to invoke `Random.shuffle` in a pipeline.
     *
     * @since 1.0
     */
   def shuffle[C](using BuildFrom[elements.type, A, C]): C = rand.shuffle(elements)
