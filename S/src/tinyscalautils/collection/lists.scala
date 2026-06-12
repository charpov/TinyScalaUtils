package tinyscalautils.collection

import java.util
import scala.jdk.CollectionConverters.SeqHasAsJava

trait JavaListFactory[C[t] <: util.List[t]]:
   /** Makes a Java list out of a sequence.
     *
     * @since 1.2
     */
   def from[A](seq: Seq[A]): C[A]

object ArrayList:
   /** A factory for `ArrayList`.
     *
     * @since 1.2
     */
   given factory: JavaListFactory[util.ArrayList] with
      def from[A](seq: Seq[A]): util.ArrayList[A] = util.ArrayList(seq.asJava)

object LinkedList:
   /** A factory for `LinkedList`.
     *
     * @since 1.2
     */
   given factory: JavaListFactory[util.LinkedList] with
      def from[A](seq: Seq[A]): util.LinkedList[A] = util.LinkedList(seq.asJava)

object JavaList:
   /** Similar to `List.of` but produces a mutable list.
     *
     * @since 1.2
     */
   def of[A, C[t] <: util.List[t]](values: A*)(using factory: JavaListFactory[C]): C[A] =
      factory.from(values)

object JavaListFactory:
   /** Lists are `ArrayList` by default.
     *
     * @since 1.2
     */
   given JavaListFactory[util.ArrayList] = ArrayList.factory
