package tinyscalautils.util

import tinyscalautils.control.interruptibly

import java.util as ju
import java.util.SplittableRandom
import java.util.concurrent.ThreadLocalRandom
import scala.util.Random
import java.util.stream.IntStream
import java.util.stream.DoubleStream
import java.util.stream.LongStream
import scala.compiletime.uninitialized

/** Fast (but not thread-safe) random number generator.
  *
  * The implementation relies on `SplittableRandom`. Java's fancier generators are not available in
  * Java 11.
  *
  * Note that the generator is always seeded. If an unseeded generator is needed, use the
  * `FastGenerator` object directly; it can be shared harmlessly throughout an application
  * (including across threads).
  *
  * The generator returned by `self` is ''not'' thread-safe, even though it subclasses
  * `java.util.Random`.
  */
class FastRandom private (rand: ju.Random) extends Random(rand):
   def this(seed: Long) = this(SplittableRandomAdapter(seed))
   def this(seed: Int) = this(seed.toLong)
end FastRandom

/** A thread-safe, unseeded fast random number generator.
  *
  * The implementation relies on `ThreadLocalRandom`, which can be shared across threads without
  * contention. Java's fancier generators are not available in Java 11.
  *
  * Note that this generator cannot be re-seeded (the `setSeed` method throws
  * `UnsupportedOperationException`).
  */
object FastRandom extends FastRandom(ThreadLocalRandomAdapter):
   assert(java.lang.Double.parseDouble("0x1.0p-24f").doubleValue == 5.9604644775390625E-8f)

   /** Interruptible variant, same as the `interruptible` extension, but cached for performance. */
   lazy val interruptible: Random = InterruptibleRandom(this)
end FastRandom

/* It'd be nice to merge the two adapter classes into one (their methods are identical),
 * but the type for `rand` would have to be `RandomGenerator`, which does not exist in Java 11.
 */

// wrapper on a SplittableRandom, which can be seeded
private final class SplittableRandomAdapter(seed: Long) extends ju.Random(seed):
   private var rand: SplittableRandom = uninitialized // set in super constructor, via setSeed

   // Random is extended for typing reasons, but the implementation should never be used.
   override def next(bits: Int): Int = throw AssertionError("never called")

   override def setSeed(seed: Long): Unit = rand = SplittableRandom(seed)

   override def nextBytes(bytes: Array[Byte]): Unit = rand.nextBytes(bytes)

   override def nextInt(): Int = rand.nextInt()

   override def nextInt(bound: Int): Int = rand.nextInt(bound)

   override def nextLong(): Long = rand.nextLong()

   override def nextBoolean(): Boolean = rand.nextBoolean()

   // Implementation lifted from RandomGenerator.
   // 5.9604644775390625E-8f is Java's 0x1.0p-24f
   override def nextFloat(): Float = (rand.nextInt() >>> 8) * 5.9604644775390625E-8f

   override def nextDouble(): Double = rand.nextDouble()

   // No nextGaussian in Java 11. The Java 17 implementation is monstrous.
   // Using the java.util.Random implementation.

   private var nextNextGaussian: Double = uninitialized
   private var haveNextNextGaussian     = false

   override def nextGaussian(): Double =
      if haveNextNextGaussian then
         haveNextNextGaussian = false
         nextNextGaussian
      else
         var v1, v2, s = 0.0
         while s == 0.0 || s >= 1.0 do
            v1 = 2 * rand.nextDouble() - 1.0
            v2 = 2 * rand.nextDouble() - 1.0
            s = v1 * v1 + v2 * v2
         val multiplier = StrictMath.sqrt(-2.0 * StrictMath.log(s) / s)
         nextNextGaussian = v2 * multiplier
         haveNextNextGaussian = true
         v1 * multiplier
   end nextGaussian

   // Methods below are never called in the current implementation of Scala's Random.
   // They're implemented because the object can be retrieved as `self`

   override def ints(streamSize: Long): IntStream = rand.ints(streamSize)

   override def ints: IntStream = rand.ints

   override def ints(streamSize: Long, randomNumberOrigin: Int, randomNumberBound: Int): IntStream =
      rand.ints(streamSize, randomNumberOrigin, randomNumberBound)

   override def ints(randomNumberOrigin: Int, randomNumberBound: Int): IntStream =
      rand.ints(randomNumberOrigin, randomNumberBound)

   override def longs(streamSize: Long): LongStream = rand.longs(streamSize)

   override def longs: LongStream = rand.longs

   override def longs(
       streamSize: Long,
       randomNumberOrigin: Long,
       randomNumberBound: Long
   ): LongStream = rand.longs(streamSize, randomNumberOrigin, randomNumberBound)

   override def longs(randomNumberOrigin: Long, randomNumberBound: Long): LongStream =
      rand.longs(randomNumberOrigin, randomNumberBound)

   override def doubles(streamSize: Long): DoubleStream = rand.doubles(streamSize)

   override def doubles: DoubleStream = rand.doubles

   override def doubles(
       streamSize: Long,
       randomNumberOrigin: Double,
       randomNumberBound: Double
   ): DoubleStream = rand.doubles(streamSize, randomNumberOrigin, randomNumberBound)

   override def doubles(randomNumberOrigin: Double, randomNumberBound: Double): DoubleStream =
      rand.doubles(randomNumberOrigin, randomNumberBound)
end SplittableRandomAdapter

/* wrapper on ThreadLocalRandom.current, so each thread has its own */
private object ThreadLocalRandomAdapter extends ju.Random(0L):

   private def rand: ju.Random = ThreadLocalRandom.current

   // this is false during super-construction, including an initial call to setSeed
   private val initialized = true

   // Random is extended for typing reasons, but the implementation should never be used.
   override def next(bits: Int): Int = throw AssertionError("never called")

   override def setSeed(seed: Long): Unit =
      if initialized then throw UnsupportedOperationException("cannot be seeded")

   override def nextBytes(bytes: Array[Byte]): Unit = rand.nextBytes(bytes)

   override def nextInt(): Int = rand.nextInt()

   override def nextInt(bound: Int): Int = rand.nextInt(bound)

   override def nextLong(): Long = rand.nextLong()

   override def nextBoolean(): Boolean = rand.nextBoolean()

   override def nextFloat(): Float = rand.nextFloat()

   override def nextDouble(): Double = rand.nextDouble()

   override def nextGaussian(): Double = rand.nextGaussian()

   // Methods below are never called in the current implementation of Scala's Random.
   // They're implemented because the object can be retrieved as `self`

   override def ints(streamSize: Long): IntStream = rand.ints(streamSize)

   override def ints: IntStream = rand.ints

   override def ints(streamSize: Long, randomNumberOrigin: Int, randomNumberBound: Int): IntStream =
      rand.ints(streamSize, randomNumberOrigin, randomNumberBound)

   override def ints(randomNumberOrigin: Int, randomNumberBound: Int): IntStream =
      rand.ints(randomNumberOrigin, randomNumberBound)

   override def longs(streamSize: Long): LongStream = rand.longs(streamSize)

   override def longs: LongStream = rand.longs

   override def longs(
       streamSize: Long,
       randomNumberOrigin: Long,
       randomNumberBound: Long
   ): LongStream = rand.longs(streamSize, randomNumberOrigin, randomNumberBound)

   override def longs(randomNumberOrigin: Long, randomNumberBound: Long): LongStream =
      rand.longs(randomNumberOrigin, randomNumberBound)

   override def doubles(streamSize: Long): DoubleStream = rand.doubles(streamSize)

   override def doubles: DoubleStream = rand.doubles

   override def doubles(
       streamSize: Long,
       randomNumberOrigin: Double,
       randomNumberBound: Double
   ): DoubleStream = rand.doubles(streamSize, randomNumberOrigin, randomNumberBound)

   override def doubles(randomNumberOrigin: Double, randomNumberBound: Double): DoubleStream =
      rand.doubles(randomNumberOrigin, randomNumberBound)
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
