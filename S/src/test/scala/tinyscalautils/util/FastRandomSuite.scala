package tinyscalautils.util

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.control.times
import java.util.SplittableRandom
import java.util.Arrays

class FastRandomSuite extends AnyFunSuite:
   test("unseeded") {
      val rand = FastRandom
      val m    = rand.between(0, 1000)
      var n    = m + 1
      while n != m do n = rand.between(0, 1000)
   }

   test("cannot re-seed") {
      assertThrows[UnsupportedOperationException](FastRandom.setSeed(42L))
   }

   test("seeded") {
      val rand1 = FastRandom(42)
      val rand2 = SplittableRandom(42L)
      assert(rand1.nextInt() == SplittableRandom(42L).nextInt())
      rand1.setSeed(42)
      100 times {
         val a1, a2 = Array.ofDim[Byte](8)
         rand1.nextBytes(a1)
         rand2.nextBytes(a2)
         assert(java.util.Arrays.equals(a1, a2))
         assert(rand1.nextInt() == rand2.nextInt())
         assert(rand1.nextInt(1000) == rand2.nextInt(1000))
         assert(rand1.nextLong() == rand2.nextLong())
         assert(rand1.nextBoolean() == rand2.nextBoolean())
         assert(rand1.nextDouble() == rand2.nextDouble())

         assert(
           Arrays.equals(
             rand1.self.ints.limit(10).toArray,
             rand2.ints.limit(10).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.ints(10).toArray,
             rand2.ints(10).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.ints(1, 1000).limit(10).toArray,
             rand2.ints(1, 1000).limit(10).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.ints(10, 1, 1000).toArray,
             rand2.ints(10, 1, 1000).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.longs.limit(10).toArray,
             rand2.longs.limit(10).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.longs(10).toArray,
             rand2.longs(10).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.longs(1, 1000).limit(10).toArray,
             rand2.longs(1, 1000).limit(10).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.longs(10, 1, 1000).toArray,
             rand2.longs(10, 1, 1000).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.doubles.limit(10).toArray,
             rand2.doubles.limit(10).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.doubles(10).toArray,
             rand2.doubles(10).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.doubles(1.0, 2.0).limit(10).toArray,
             rand2.doubles(1.0, 2.0).limit(10).toArray
           )
         )
         assert(
           Arrays.equals(
             rand1.self.doubles(10, 1.0, 2.0).toArray,
             rand2.doubles(10, 1.0, 2.0).toArray
           )
         )
      }
   }
