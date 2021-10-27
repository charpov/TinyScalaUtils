package tinyscalautils.java

import tinyscalautils.collection.{ circular, pickOne, randomly, pickOneOption }

import scala.jdk.CollectionConverters.{ IterableHasAsScala, IteratorHasAsJava }
import scala.util.Random
import scala.jdk.OptionConverters.RichOption

private final class CollectionScala:
   def circular[A](iterable: java.lang.Iterable[A]): java.util.Iterator[A] =
      iterable.asScala.circular.asJava

   def randomly[A](iterable: java.lang.Iterable[A], rand: java.util.Random): java.util.Iterator[A] =
      iterable.asScala.randomly(using Random(rand)).asJava

   def pickOne[A](iterable: java.lang.Iterable[A], rand: java.util.Random): A =
      iterable.asScala.pickOne(using Random(rand))

   def pickOneOption[A](
       iterable: java.lang.Iterable[A],
       rand: java.util.Random
   ): java.util.Optional[A] =
      iterable.asScala.pickOneOption(using Random(rand)).toJava
