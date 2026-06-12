package tinyscalautils.collection

import scala.collection.immutable.SeqOps

private val none = (_: Any) => None

extension [A, CC[_]](seq: SeqOps[A, CC, CC[A]])
   /** A new sequence in which element at position `i` has been deleted. If `i` is not a valid
     * index, no exception is thrown and the sequence is returned unchanged (usually as the same
     * instance).
     *
     * @since 1.5.0
     */
   def deleted(i: Int): CC[A] = updatedWith(i)(none)

   /** A new sequence in which element at position `i` has been replaced with a new value, if any.
     * The new value is computed as `f(seq(i))`. If it doesn't produce a replacement value, the
     * element at position `i` is simply removed. If `i` is not a valid index, no exception is
     * thrown and the sequence is returned unchanged (usually as the same instance).
     *
     * @since 1.5.0
     */
   def updatedWith[B >: A](i: Int)(f: A => Option[B]): CC[B] =
      seq.iterableFactory.from:
         if i < 0 then seq
         else
            val (left, right) = seq.view.splitAt(i)
            if right.isEmpty then seq
            else if f eq none then left ++ right.tail
            else left ++ f(seq(i)) ++ right.tail
