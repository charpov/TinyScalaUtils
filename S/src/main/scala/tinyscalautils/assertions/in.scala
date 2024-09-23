package tinyscalautils.assertions

import scala.collection.{ Seq, Set, Map }

extension [A](element: A)
   @deprecated("use collection.in instead", since = "1.5.0")
   infix def in(collection: Iterable[A]): Boolean = collection match
      case seq: Seq[?]         => seq.contains(element)
      case set: Set[? >: A]    => set.contains(element)
      case map: Map[? >: A, ?] => map.contains(element)
      case iterable            => iterable.iterator.contains(element)
