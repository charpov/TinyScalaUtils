package tinyscalautils.collection

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.text.CharLetters.*

class DeletedSuite extends AnyFunSuite:
   test("deleted List"):
      val list = List(A, B, C)
      assert(list.deleted(-1) eq list)
      assert(list.deleted(3) eq list)
      assert(list.deleted(0) == List(B, C))
      assert(list.deleted(1) == List(A, C))
      assert(list.deleted(2) == List(A, B))
      assert(list.deleted(1).isInstanceOf[List[?]])

   test("deleted Vector"):
      val vector = Vector(A, B, C)
      assert(vector.deleted(-1) eq vector)
      assert(vector.deleted(3) eq vector)
      assert(vector.deleted(0) == Vector(B, C))
      assert(vector.deleted(1) == Vector(A, C))
      assert(vector.deleted(2) == Vector(A, B))
      assert(vector.deleted(1).isInstanceOf[Vector[?]])

   test("deleted Nil"):
      assert(Nil.deleted(-1) eq Nil)
      assert(Nil.deleted(0) eq Nil)
      assert(Nil.deleted(1) eq Nil)

   test("deleted Range"):
      val range = 1 to 5
      assert(range.deleted(-1) eq range)
      assert(range.deleted(5) eq range)
      assert(range.deleted(2) == Seq(1, 2, 4, 5))
      range.iterableFactory

   test("updated same type"):
      def f(c: Char) = Option.when(c != B)(X)
      val list       = List(A, B, C)
      assert(list.updatedWith(-1)(f) eq list)
      assert(list.updatedWith(3)(f) eq list)
      assert(list.updatedWith(0)(f) == List(X, B, C))
      assert(list.updatedWith(1)(f) == List(A, C))
      assert(list.updatedWith(2)(f) == List(A, B, X))
      assert(list.updatedWith(1)(f).isInstanceOf[List[?]])

   test("updated different type"):
      def f(c: Char) = Option.when(c != B)(0)
      val list       = List(A, B, C)
      assert(list.updatedWith(-1)(f) eq list)
      assert(list.updatedWith(3)(f) eq list)
      assert(list.updatedWith(0)(f) == List(0, B, C))
      assert(list.updatedWith(1)(f) == List(A, C))
      assert(list.updatedWith(2)(f) == List(A, B, 0))
      assert(list.updatedWith(1)(f).isInstanceOf[List[?]])

   test("laziness"):
      val list = LazyList.continually(X)
      object f extends (Char => Option[Char]):
         var callCount = 0
         def apply(c: Char) =
            callCount += 1
            Some(c.toLower)
      end f
      assert(list.updatedWith(-1)(f) eq list)
      assert(f.callCount == 0)
      val updated = list.updatedWith(5)(f)
      assert(f.callCount == 1)
      assert(updated.take(10).mkString == "XXXXXxXXXX")

   test("demo"):
      val list = List(A, B, C)
      assert(list.updatedWith(1)(c => Some(c.toInt)) == List(A, 66, C))
      assert(list.updatedWith(1)(c => Option.when(c > Z)(c.toInt)) == List(A, C))
      assert(list.deleted(1) == List(A, C))
