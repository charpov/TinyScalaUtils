package tinyscalautils.collection

import tinyscalautils.assertions.require

import scala.collection.IndexedSeq
import scala.util.Random
import java.util.NoSuchElementException

extension [A](collection: Iterable[A])
   /** Picks one element at random.
     *
     * This should not be used in a loop; use [[randomly]] instead.
     *
     * The collection is assumed non-emtpy.
     *
     * @since 1.0
     */
   @throws[NoSuchElementException]("if the collection is empty")
   def pickOne(using rand: Random): A =
      if collection.isEmpty then throw NoSuchElementException("empty.pickOne")
      collection match
         // both apply and length are efficient on IndexedSeq
         case seq: IndexedSeq[A] => seq(rand.nextInt(seq.length))
         case _                  => collection.iterator.drop(rand.nextInt(collection.size)).next()

   /** Picks one element at random.
     *
     * This should not be used in a loop; use [[randomly]] instead.
     *
     * Returns `None` if the collection is empty.
     *
     * @since 1.0
     */
   def pickOneOption(using Random): Option[A] =
      if collection.isEmpty then None else Some(pickOne)
