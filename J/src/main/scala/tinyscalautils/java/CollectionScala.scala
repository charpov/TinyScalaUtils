package tinyscalautils.java

import tinyscalautils.collection.{ circular, pickOne, pickOneOption, randomly }
import tinyscalautils.util.RandomAdapter

import java.{ lang as jl, util as ju }
import java.util.random.RandomGenerator
import scala.jdk.CollectionConverters.{ IterableHasAsScala, IteratorHasAsJava }
import scala.jdk.OptionConverters.RichOption
import scala.util.Random

private final class CollectionScala:
   def circular[A](iterable: jl.Iterable[A]): ju.Iterator[A] = iterable.asScala.circular.asJava

   def randomly[A](iterable: jl.Iterable[A], rand: RandomGenerator): ju.Iterator[A] =
      iterable.asScala.randomly(using Random(adapt(rand))).asJava

   def pickOne[A](iterable: java.lang.Iterable[A], rand: RandomGenerator): A =
      iterable.asScala.pickOne(using Random(adapt(rand)))

   def pickOneOption[A](iterable: jl.Iterable[A], rand: RandomGenerator): ju.Optional[A] =
      iterable.asScala.pickOneOption(using Random(adapt(rand))).toJava

   private def adapt(randGen: RandomGenerator): ju.Random =
      randGen match
         case rand: ju.Random => rand
         case _               => RandomGeneratorAdapter(randGen)

   private final class RandomGeneratorAdapter(randGen: RandomGenerator) extends RandomAdapter:
      protected def rand = randGen
