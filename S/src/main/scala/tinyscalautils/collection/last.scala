package tinyscalautils.collection

import java.util.NoSuchElementException
import scala.collection.IterableOps
import scala.compiletime.asMatchable

extension [A](iterable: IterableOnce[A])
   /** The last element of the collection, if any.
     *
     * @throws NoSuchElementException
     *   if the collection is empty.
     *
     * @since 1.5.0
     */
   def last: A =
      iterable.asMatchable match
         case iterableOps: IterableOps[? <: A, ?, ?] => iterableOps.last
         case _ =>
            val i = iterable.iterator
            if i.isEmpty then throw NoSuchElementException("last of empty iterable")
            var value = i.next()
            while i.hasNext do value = i.next()
            value

   /** The last element of the collection, if any.
     *
     * @since 1.5.0
     */
   def lastOption: Option[A] =
      // written to avoid iterating on Array and IArray
      Option.unless(iterable.knownSize == 0 || iterable.iterator.isEmpty)(iterable.last)
