package tinyscalautils.util

import tinyscalautils.control.interruptibly

import java.util as ju
import java.util.SplittableRandom
import java.util.concurrent.ThreadLocalRandom
import java.util.random.RandomGenerator
import java.util.stream.{ DoubleStream, IntStream, LongStream }
import scala.compiletime.uninitialized
import scala.util.Random

/** Fast (but not thread-safe) random number generator.
  *
  * The implementation relies on `SplittableRandom`. Java's fancier generators are not used.
  *
  * The generator is always seeded. If an unseeded generator is needed, use the `FastGenerator`
  * object directly; it can be shared harmlessly throughout an application (including across
  * threads).
  *
  * @note
  *   The generator returned by `self` is ''not'' thread-safe, even though it subclasses
  *   `java.util.Random`.
  */
class FastRandom private (rand: ju.Random) extends Random(rand):
   def this(seed: Long) = this(SplittableRandomAdapter(seed))
   def this(seed: Int) = this(seed.toLong)
end FastRandom

/** A thread-safe, unseeded fast random number generator.
  *
  * The implementation relies on `ThreadLocalRandom`, which can be shared across threads without
  * contention. Java's fancier generators are not used.
  *
  * This generator cannot be re-seeded (the `setSeed` method throws
  * `UnsupportedOperationException`).
  */
object FastRandom extends FastRandom(ThreadLocalRandomAdapter):
   /** Interruptible variant, same as the `interruptible` extension, but cached for performance. */
   lazy val interruptible: Random = InterruptibleRandom(this)
end FastRandom

private abstract class RandomAdapter(seed: Long) extends ju.Random(seed):
   // _rand is set in super constructor, via setSeed.
   // must use uninitialized because an explicit assignment to null would overwrite the value
   // set by setSeed.
   protected var _rand: RandomGenerator = uninitialized

   protected def rand: RandomGenerator

   // Random is extended for typing reasons, but the implementation should never be used.
   override def next(bits: Int): Int = throw AssertionError("never called")

   override def nextBytes(bytes: Array[Byte]): Unit = rand.nextBytes(bytes)

   override def nextInt(): Int = rand.nextInt()

   override def nextInt(bound: Int): Int = rand.nextInt(bound)

   override def nextLong(): Long = rand.nextLong()

   override def nextBoolean(): Boolean = rand.nextBoolean()

   override def nextFloat(): Float = rand.nextFloat()

   override def nextDouble(): Double = rand.nextDouble()

   override def nextGaussian(): Double = rand.nextGaussian()

   override def ints(streamSize: Long, randomNumberOrigin: Int, randomNumberBound: Int): IntStream =
      rand.ints(streamSize, randomNumberOrigin, randomNumberBound)

   override def ints(randomNumberOrigin: Int, randomNumberBound: Int): IntStream =
      rand.ints(randomNumberOrigin, randomNumberBound)

   override def ints(streamSize: Long): IntStream = rand.ints(streamSize)

   override def ints(): IntStream = rand.ints()

   override def longs(
       streamSize: Long,
       randomNumberOrigin: Long,
       randomNumberBound: Long
   ): LongStream = rand.longs(streamSize, randomNumberOrigin, randomNumberBound)

   override def longs(randomNumberOrigin: Long, randomNumberBound: Long): LongStream =
      rand.longs(randomNumberOrigin, randomNumberBound)

   override def longs(streamSize: Long): LongStream = rand.longs(streamSize)

   override def longs(): LongStream = rand.longs()

   override def doubles(
       streamSize: Long,
       randomNumberOrigin: Double,
       randomNumberBound: Double
   ): DoubleStream = rand.doubles(streamSize, randomNumberOrigin, randomNumberBound)

   override def doubles(randomNumberOrigin: Double, randomNumberBound: Double): DoubleStream =
      rand.doubles(randomNumberOrigin, randomNumberBound)

   override def doubles(streamSize: Long): DoubleStream = rand.doubles(streamSize)

   override def doubles(): DoubleStream = rand.doubles()
end RandomAdapter

// wrapper on a SplittableRandom, which can be seeded
private final class SplittableRandomAdapter(seed: Long) extends RandomAdapter(seed):
   // super.setSeed(seed) would be needed to reset haveNextNextGaussian, but the value doesn't
   // matter because we bypass the nextGaussian implementation from Random
   override def setSeed(seed: Long): Unit = _rand = SplittableRandom(seed)
   def rand                               = _rand
end SplittableRandomAdapter

/* wrapper on ThreadLocalRandom.current, so each thread has its own */
private object ThreadLocalRandomAdapter extends RandomAdapter(0L):
   protected def rand = ThreadLocalRandom.current

   // _rand is null during super-construction, including an initial call to setSeed
   override def setSeed(seed: Long): Unit =
      if _rand ne null then throw UnsupportedOperationException("cannot be seeded")
      _rand = Random.self // anything not null; never used
end ThreadLocalRandomAdapter

private final class InterruptibleRandom(rand: Random) extends Random(rand.self):
   override def nextBoolean(): Boolean              = interruptibly(rand.nextBoolean())
   override def nextBytes(bytes: Array[Byte]): Unit = interruptibly(rand.nextBytes(bytes))
   override def nextBytes(n: Int): Array[Byte]      = interruptibly(rand.nextBytes(n))
   override def nextDouble(): Double                = interruptibly(rand.nextDouble())
   override def nextFloat(): Float                  = interruptibly(rand.nextFloat())
   override def nextGaussian(): Double              = interruptibly(rand.nextGaussian())
   override def nextInt(): Int                      = interruptibly(rand.nextInt())
   override def nextInt(n: Int): Int                = interruptibly(rand.nextInt(n))
   override def nextLong(): Long                    = interruptibly(rand.nextLong())
   // nextPrintableChar is overridden because it uses self.nextInt instead of nextInt
   override def nextPrintableChar(): Char = interruptibly(rand.nextPrintableChar())
   override def setSeed(seed: Long): Unit = interruptibly(rand.setSeed(seed))
   // alphanumeric is overridden because it uses self.nextInt instead of nextInt
   override def alphanumeric: LazyList[Char] = interruptibly(rand.alphanumeric)
end InterruptibleRandom

extension (rand: Random)
   /** A wrapper that calls all the `Random` methods interruptibly. The new generator is as
     * thread-safe as the original.
     */
   def interruptible: Random = InterruptibleRandom(rand)
