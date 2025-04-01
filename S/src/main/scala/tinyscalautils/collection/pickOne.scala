package tinyscalautils.collection

import java.util.NoSuchElementException
import scala.util.Random
import scala.collection.Seq

extension [A](collection: Iterable[A])
   /** Picks one element at random.
     *
     * This should not be used in a loop; use [[randomly]] instead.
     *
     * The collection is assumed non-emtpy.
     *
     * @throws NoSuchElementException
     *   if the collection is empty.
     *
     * @since 1.0
     */
   def pickOne(using rand: Random): A =
      if collection.isEmpty then throw NoSuchElementException("empty.pickOne")
      collection match
         case seq: Seq[A] => seq(rand.nextInt(seq.length))
         case _           => collection.iterator.drop(rand.nextInt(collection.size)).next()

   /** Picks one element at random.
     *
     * This should not be used in a loop; use [[randomly]] instead.
     *
     * Returns `None` if the collection is empty.
     *
     * @since 1.0
     */
   def pickOneOption(using Random): Option[A] = Option.unless(collection.isEmpty)(pickOne)
