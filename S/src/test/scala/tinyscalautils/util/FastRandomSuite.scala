package tinyscalautils.util

import org.scalactic.Equality
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.control.times

import java.util.SplittableRandom
import java.util.stream.{ BaseStream, DoubleStream, IntStream, LongStream }
import scala.compiletime.asMatchable
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.Random

class FastRandomSuite extends AnyFunSuite:
   private def same(x: BaseStream[?, ?], other: Any) =
      other.asMatchable match
         case y: BaseStream[?, ?] =>
            x.iterator.asScala.take(10).sameElements(y.iterator.asScala.take(10))
         case _ => false

   private given Equality[IntStream] with
      def areEqual(x: IntStream, value: Any): Boolean = same(x, value)

   private given Equality[LongStream] with
      def areEqual(x: LongStream, value: Any): Boolean = same(x, value)

   private given Equality[DoubleStream] with
      def areEqual(x: DoubleStream, value: Any): Boolean = same(x, value)

   test("unseeded"):
      val rand = FastRandom
      val m    = rand.between(0, 1000)
      var n    = m + 1
      while n != m do n = rand.between(0, 1000)

   test("cannot re-seed"):
      assertThrows[UnsupportedOperationException](FastRandom.setSeed(42L))

   test("types"):
      assert(FastRandom.self eq ThreadLocalRandomAdapter)
      assert(FastRandom(42).self.isInstanceOf[SplittableRandomAdapter])

   test("seeded 1"):
      val rand = FastRandom(42)
      val n    = rand.nextInt()
      rand.setSeed(42)
      assert(rand.nextInt() == n)

   test("seeded 2"):
      val rand1 = FastRandom(42)
      val rand2 = SplittableRandom(42L)
      assert(rand1.nextInt() == SplittableRandom(42L).nextInt())
      rand1.setSeed(42)
      100 times:
         val a1, a2 = Array.ofDim[Byte](8)
         rand1.nextBytes(a1)
         rand2.nextBytes(a2)
         assert(a1 === a2)
         assert(rand1.nextInt() == rand2.nextInt())
         assert(rand1.nextInt(1000) == rand2.nextInt(1000))
         assert(rand1.nextLong() == rand2.nextLong())
         assert(rand1.nextBoolean() == rand2.nextBoolean())
         assert(rand1.nextDouble() == rand2.nextDouble())
         assert(rand1.nextFloat() == rand2.nextFloat())
         assert(rand1.nextGaussian() == rand2.nextGaussian())

         assert(rand1.self.ints === rand2.ints)
         assert(rand1.self.ints(10) === rand2.ints(10))
         assert(rand1.self.ints(1, 1000) === rand2.ints(1, 1000))
         assert(rand1.self.ints(10, 1, 1000) === rand2.ints(10, 1, 1000))
         assert(rand1.self.longs === rand2.longs)
         assert(rand1.self.longs(10) === rand2.longs(10))
         assert(rand1.self.longs(1, 1000) === rand2.longs(1, 1000))
         assert(rand1.self.longs(10, 1, 1000) === rand2.longs(10, 1, 1000))
         assert(rand1.self.doubles === rand2.doubles)
         assert(rand1.self.doubles(10) === rand2.doubles(10))
         assert(rand1.self.doubles(1.0, 2.0) === rand2.doubles(1.0, 2.0))
         assert(rand1.self.doubles(10, 1.0, 2.0) === rand2.doubles(10, 1.0, 2.0))

   test("seeded 3"):
      def sample(rand: Random) =
         Seq(
           rand.nextInt(),
           rand.nextInt(100),
           rand.nextLong(),
           rand.nextLong(100L),
           rand.nextFloat(),
           rand.nextDouble(),
           rand.nextGaussian(),
           rand.nextBoolean(),
           rand.nextBytes(10).toSeq,
           rand.alphanumeric.take(10).toList,
           rand.nextPrintableChar(),
           rand.nextString(10),
           rand.between(1, 10),
           rand.between(1L, 10L),
           rand.between(1.0, 10.0),
           rand.between(1.0f, 10.0f),
         )
      val rand = FastRandom(42)
      val seq1 = sample(rand)
      rand.setSeed(42)
      val seq2 = sample(rand)
      assert(seq1 == seq2)

   for (r, s) <- Seq(
        (() => FastRandom, "FastRandom"),
        (() => FastRandom(8), "FastRandom(8)"),
        (() => Random(8), "Random(8)"),
      )
   do
      test(s"interrupts ($s)"):
         def check(rand: () => Random, call: Random => Matchable) =
            val rand1 = rand()
            val rand2 = rand().interruptible
            if rand1 ne FastRandom then assert(call(rand1) === call(rand2))
            Thread.currentThread.interrupt()
            assertThrows[InterruptedException](call(rand2))
         end check
         check(r, _.nextBoolean())
         check(r, _.nextBytes(Array.ofDim[Byte](8)))
         check(r, _.nextBytes(8))
         check(r, _.nextDouble())
         check(r, _.between(1.0, 8.0))
         check(r, _.nextFloat())
         check(r, _.between(1f, 8f))
         check(r, _.nextGaussian())
         check(r, _.nextInt())
         check(r, _.nextInt(8))
         check(r, _.between(1, 8))
         check(r, _.nextLong())
         check(r, _.nextLong(8L))
         check(r, _.between(1L, 8L))
         check(r, _.nextString(8))
         check(r, _.nextPrintableChar())
         check(r, _.shuffle(Seq(1, 2, 3)))
         check(r, _.alphanumeric.take(10))
         check(
           r,
           rand =>
              try rand.setSeed(8L)
              catch case _: UnsupportedOperationException => ()
         )

   test("stable"):
      val r = FastRandom(42)
      assertResult(
        Seq(
          true, 909395113, 81, 0.76145077f, 0.03803016854024621, -31, 4028864712777624925L, 19L,
          0.6246355380734417
        )
      ):
         Seq(
           r.nextBoolean(),
           r.nextInt(),
           r.nextInt(100),
           r.nextFloat(),
           r.nextDouble(),
           r.nextBytes(2).sum,
           r.nextLong(),
           r.nextLong(100),
           r.nextGaussian()
         )
