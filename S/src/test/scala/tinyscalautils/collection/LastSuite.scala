package tinyscalautils.collection

import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.{ SortedMap, SortedSet }

class LastSuite extends AnyFunSuite:
   test("last"):
      assert(Iterator(1).last == 1)
      assert(Iterator(1, 2, 3).last == 3)
      assertThrows[NoSuchElementException](Iterator.empty.last)

   test("lastOption"):
      assert(Iterator(1).lastOption.contains(1))
      assert(Iterator(1, 2, 3).lastOption.contains(3))
      assert(Iterator.empty.lastOption.isEmpty)
      assert(Array.empty[String].lastOption.isEmpty)
      assert(IArray.empty[String].lastOption.isEmpty)
      assert(List.empty.lastOption.isEmpty)
      assert(Map.empty.lastOption.isEmpty)
      assert(Seq.empty.lastOption.isEmpty)

   test("using last method"):
      assert(List(1, 2, 3).last == 3)
      assert(List(1, 2, 3).lastOption.contains(3))
      assert(Array(1, 2, 3).last == 3)
      assert(Array(1, 2, 3).lastOption.contains(3))
      assert(IArray(1, 2, 3).last == 3)
      assert(IArray(1, 2, 3).lastOption.contains(3))
      assert(SortedMap(3 -> "C", 2 -> "B", 1 -> "A").last == (3, "C"))
      assert(SortedMap(3 -> "C", 2 -> "B", 1 -> "A").lastOption.contains((3, "C")))
      assert(SortedSet(3, 2, 1).last == 3)
      assert(SortedSet(3, 2, 1).lastOption.contains(3))
