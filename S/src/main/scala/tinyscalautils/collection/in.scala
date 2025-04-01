package tinyscalautils.collection

import scala.collection.{ Map, Seq, Set }

extension [A](element: A)
   /** Reverses the order of `contains`.
     *
     * `value in collection` replaces `collection contains value`.
     *
     * @since 1.0
     */
   infix def in(collection: Iterable[A]): Boolean =
      collection match
         case seq: Seq[?]         => seq.contains(element)
         case set: Set[? >: A]    => set.contains(element)
         case map: Map[? >: A, ?] => map.contains(element)
         case iterable            => iterable.iterator.contains(element)
