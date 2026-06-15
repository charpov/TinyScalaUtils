package tinyscalautils.collection

import scala.collection.SeqOps

extension [A, CC[_]](self: SeqOps[A, CC, CC[A]])
   /** Sorted according to implicit order, but in reverse. */
   def sortedInReverse[B >: A : Ordering as ordering]: CC[A] = self.sorted(using ordering.reverse)
