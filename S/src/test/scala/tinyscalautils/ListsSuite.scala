package tinyscalautils

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.collection.{JavaList, LinkedList}

import java.util

class ListsSuite extends AnyFunSuite:
   test("ArrayList"):
      val list = JavaList.of("X", "Y")
      assert(list.getClass == classOf[util.ArrayList[?]])
      assert(list.add("Z"))
      assert(list == util.List.of("X", "Y", "Z"))

   test("LinkedList 1"):
      val list = JavaList.of("X", "Y")(using LinkedList.factory)
      assert(list.getClass == classOf[util.LinkedList[?]])
      assert(list.add("Z"))
      assert(list == util.List.of("X", "Y", "Z"))

   test("LinkedList 2"):
      import LinkedList.factory

      val list = JavaList.of("X", "Y")
      assert(list.getClass == classOf[util.LinkedList[?]])
      assert(list.add("Z"))
      assert(list == util.List.of("X", "Y", "Z"))
