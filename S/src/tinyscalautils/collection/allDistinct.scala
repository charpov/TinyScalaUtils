package tinyscalautils.collection

import scala.annotation.tailrec
import scala.collection.mutable

extension [A](collection: IterableOnce[A])
   /** True if all the collection's elements are distinct.
     *
     * Note that this is true of an empty collection.
     *
     * @since 1.7
     */
   def allDistinct: Boolean =
      @tailrec
      def check(values: mutable.Set[A], iterator: Iterator[A]): Boolean =
         iterator.isEmpty || (values.add(iterator.next()) && check(values, iterator))
      check(mutable.Set.empty, collection.iterator)
