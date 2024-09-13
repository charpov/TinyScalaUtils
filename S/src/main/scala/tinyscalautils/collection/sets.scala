package tinyscalautils.collection

import java.util
import scala.jdk.CollectionConverters.SeqHasAsJava

trait JavaSetFactory[C[t] <: util.Set[t]]:
   /** Makes a Java set out of a sequence.
     *
     * @since 1.3.1
     */
   def from[A](seq: Seq[A]): C[A]

object HashSet:
   /** A factory for `HashSet`.
     *
     * @since 1.3.1
     */
   given factory: JavaSetFactory[util.HashSet] with
      def from[A](seq: Seq[A]): util.HashSet[A] = util.HashSet(seq.asJava)

object TreeSet:
   /** A factory for `TreeSet`.
     *
     * @since 1.3.1
     */
   given factory: JavaSetFactory[util.TreeSet] with
      def from[A](seq: Seq[A]): util.TreeSet[A] = util.TreeSet(seq.asJava)

object JavaSet:
   /** Similar to `Set.of` but produces a mutable set.
     *
     * @since 1.3.1
     */
   def of[A, C[t] <: util.Set[t]](values: A*)(using factory: JavaSetFactory[C]): C[A] =
      factory.from(values)

object JavaSetFactory:
   /** Sets are `HashSet` by default.
     *
     * @since 1.3.1
     */
   given JavaSetFactory[util.HashSet] = HashSet.factory
