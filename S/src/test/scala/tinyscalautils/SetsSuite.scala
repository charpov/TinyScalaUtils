package tinyscalautils

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.collection.{ JavaSet, TreeSet }

import java.util

class SetsSuite extends AnyFunSuite:
   test("HashSet"):
      val set = JavaSet.of("X", "Y")
      assert(set.getClass == classOf[util.HashSet[?]])
      assert(set.add("Z"))
      assert(set == util.Set.of("X", "Y", "Z"))

   test("TreeSet 1"):
      val set = JavaSet.of("X", "Y")(using TreeSet.factory)
      assert(set.getClass == classOf[util.TreeSet[?]])
      assert(set.add("Z"))
      assert(set == util.Set.of("X", "Y", "Z"))

   test("TreeSet 2"):
      import TreeSet.factory

      val set = JavaSet.of("X", "Y")
      assert(set.getClass == classOf[util.TreeSet[?]])
      assert(set.add("Z"))
      assert(set == util.Set.of("X", "Y", "Z"))
