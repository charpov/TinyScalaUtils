package tinyscalautils.timing

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.threads.timeoutTimer

class RunForSuite extends AnyFunSuite with Tolerance:
   private def run(n: Int): Option[Int] = Option.when(n > 0)(delay(0.5)(n - 1))

   private def callOption(n: Int): Option[(Int, Int)] = run(n).map(n -> _)

   private def callPair(n: Int): (Int, Option[Int]) = n -> run(n)

   private def runBool(n: Int): () => Boolean =
      var m = n
      () =>
         val opt = run(m)
         for v <- opt do m = v
         opt.nonEmpty

   test("runFor 1"):
      val (r, time) = timeIt(runFor(-1.0)(5)(run))
      assert(!r)
      assert(time === 0.0 +- 0.1)

   test("runFor 2"):
      val (r, time) = timeIt(runFor(0.0)(5)(run))
      assert(!r)
      assert(time === 0.5 +- 0.1)

   test("runFor 3"):
      val (r, time) = timeIt(runFor(0.0)(0)(run))
      assert(r)
      assert(time === 0.0 +- 0.1)

   test("runFor 4"):
      val (r, time) = timeIt(runFor(1.5)(2)(run))
      assert(r)
      assert(time === 1.0 +- 0.1)

   test("runFor 5"):
      val (r, time) = timeIt(runFor(0.9)(2)(run))
      assert(!r)
      assert(time === 1.0 +- 0.1)

   test("runFor simple 1"):
      val (r, time) = timeIt(runFor(-1.0)(runBool(5)))
      assert(!r)
      assert(time === 0.0 +- 0.1)

   test("runFor simple 2"):
      val (r, time) = timeIt(runFor(0.0)(runBool(5)))
      assert(!r)
      assert(time === 0.5 +- 0.1)

   test("runFor simple 3"):
      val (r, time) = timeIt(runFor(0.0)(runBool(0)))
      assert(r)
      assert(time === 0.0 +- 0.1)

   test("runFor simple 4"):
      val (r, time) = timeIt(runFor(1.5)(runBool(2)))
      assert(r)
      assert(time === 1.0 +- 0.1)

   test("runFor simple 5"):
      val (r, time) = timeIt(runFor(0.9)(runBool(2)))
      assert(!r)
      assert(time === 1.0 +- 0.1)

   test("callFor option 1"):
      val ((s, r), time) = timeIt(callFor(-1.0)(5)(callOption))
      assert(s.isEmpty)
      assert(!r)
      assert(time === 0.0 +- 0.1)

   test("callFor option 2"):
      val ((s, r), time) = timeIt(callFor(0.0)(5)(callOption))
      assert(s == Seq(5))
      assert(!r)
      assert(time === 0.5 +- 0.1)

   test("callFor option 3"):
      val ((s, r), time) = timeIt(callFor(0.0)(0)(callOption))
      assert(s.isEmpty)
      assert(r)
      assert(time === 0.0 +- 0.1)

   test("callFor option 4"):
      val ((s, r), time) = timeIt(callFor(1.5)(2)(callOption))
      assert(s == Seq(2, 1))
      assert(r)
      assert(time === 1.0 +- 0.1)

   test("callFor option 5"):
      val ((s, r), time) = timeIt(callFor(0.9)(2)(callOption))
      assert(s == Seq(2, 1))
      assert(!r)
      assert(time === 1.0 +- 0.1)

   test("callFor pair 1"):
      val ((s, r), time) = timeIt(callFor(-1.0)(5)(callPair))
      assert(s.isEmpty)
      assert(!r)
      assert(time === 0.0 +- 0.1)

   test("callFor pair 2"):
      val ((s, r), time) = timeIt(callFor(0.0)(5)(callPair))
      assert(s == Seq(5))
      assert(!r)
      assert(time === 0.5 +- 0.1)

   test("callFor pair 3"):
      val ((s, r), time) = timeIt(callFor(0.0)(0)(callPair))
      assert(s == Seq(0))
      assert(r)
      assert(time === 0.0 +- 0.1)

   test("callFor pair 4"):
      val ((s, r), time) = timeIt(callFor(1.5)(2)(callPair))
      assert(s == Seq(2, 1, 0))
      assert(r)
      assert(time === 1.0 +- 0.1)

   test("callFor pair 5"):
      val ((s, r), time) = timeIt(callFor(0.9)(2)(callPair))
      assert(s == Seq(2, 1))
      assert(!r)
      assert(time === 1.0 +- 0.1)

   test("callFor simple 1"):
      val ((s, r), time) = timeIt(callFor(-1.0)(5)(run))
      assert(s.isEmpty)
      assert(!r)
      assert(time === 0.0 +- 0.1)

   test("callFor simple 2"):
      val ((s, r), time) = timeIt(callFor(0.0)(5)(run))
      assert(s == Seq(4))
      assert(!r)
      assert(time === 0.5 +- 0.1)

   test("callFor simple 3"):
      val ((s, r), time) = timeIt(callFor(0.0)(0)(run))
      assert(s.isEmpty)
      assert(r)
      assert(time === 0.0 +- 0.1)

   test("callFor simple 4"):
      val ((s, r), time) = timeIt(callFor(1.5)(2)(run))
      assert(s == Seq(1, 0))
      assert(r)
      assert(time === 1.0 +- 0.1)

   test("callFor simple 5"):
      val ((s, r), time) = timeIt(callFor(0.9)(2)(run))
      assert(s == Seq(1, 0))
      assert(!r)
      assert(time === 1.0 +- 0.1)
