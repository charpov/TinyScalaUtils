package tinyscalautils.collection

import scala.collection.AbstractIterator
import scala.util.Random

private final class RandomElements[A](sequence: IndexedSeq[A], rand: Random)
    extends AbstractIterator[A]:
   def hasNext: Boolean = true
   def next(): A        = sequence(rand.nextInt(sequence.length))

extension [A](elements: IterableOnce[A])
   /** An iterator that produces the elements of a collection in a random (uniform) order.
     *
     * If the collection is empty, the iterator is empty; otherwise, it is infinite.
     *
     * The iterator is thread-safe if the random number generator is thread-safe.
     *
     * @since 1.0
     */
   def randomly(using rand: Random): Iterator[A] =
      val sequence = elements.iterator.toIndexedSeq
      if sequence.isEmpty then Iterator.empty else RandomElements(sequence, rand)
