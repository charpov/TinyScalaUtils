package tinyscalautils.util

import tinyscalautils.assertions.require

import scala.collection.IndexedSeq
import scala.util.Random
import java.util.NoSuchElementException

extension [A](collection: Iterable[A])
   /** Picks one element at random.
     *
     * The collection is assumed non-emtpy
     */
   @throws[NoSuchElementException]("if the collection is empty")
   def pickOne(using rand: Random): A =
      if collection.isEmpty then throw NoSuchElementException("empty.pickOne")
      collection match
         // both apply and length are efficient on IndexedSeq
         case seq: IndexedSeq[A] => seq(rand.between(0, seq.length))
         case _ => collection.iterator.drop(rand.between(0, collection.size)).next()

   /** Picks one element at random.
     *
     * Returns `None` if the collection is empty.
     */
   def pickOneOption(using Random): Option[A] =
      if collection.isEmpty then None else Some(pickOne)
