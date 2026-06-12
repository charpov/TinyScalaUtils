package tinyscalautils.io

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.util.FastRandom

import java.nio.file.{ Files, Path }

class OutputSuite extends AnyFunSuite:
   private def makeFile(): Path =
      val path = Files.createTempFile("tiny", ".txt")
      path.toFile.deleteOnExit()
      path

   private val str  = FastRandom.nextString(10_000)
   private val list = List(1, 2, 3)

   test("demo"):
      val file = makeFile()
      write(file)(list.toString)
      assert(Files.readString(file) == "List(1, 2, 3)")
      write(file, newline = true)(list.toString)
      assert(Files.readString(file) == "List(1, 2, 3)\n")
      writeAll(file)(list)
      assert(Files.readString(file) == "1\n2\n3\n")
      writeAll(sep = ",")(file)(list)
      assert(Files.readString(file) == "1,2,3")
      writeAll(pre = "[", sep = ",", post = "]")(file)(list)
      assert(Files.readString(file) == "[1,2,3]")
      writeAll()(file)(list)
      assert(Files.readString(file) == "123")

   test("write 1"):
      val path = makeFile()
      write(path)(str)
      assert(Files.readString(path) == str)

   test("write 2"):
      val path = makeFile()
      write(path, newline = true)(str)
      assert(Files.readString(path) == str + '\n')

   test("write 3"):
      val path = makeFile()
      write(path.toFile)(str)
      assert(Files.readString(path) == str)

   test("write 4"):
      val path = makeFile()
      write(path.toString)(str)
      assert(Files.readString(path) == str)

   test("write 5"):
      val path = makeFile()
      write(Files.newOutputStream(path))(str)
      assert(Files.readString(path) == str)

   test("writeAll 1"):
      val path = makeFile()
      writeAll(sep = ",")(path)(list)
      assert(Files.readString(path) == "1,2,3")

   test("writeAll 2"):
      val path = makeFile()
      writeAll(pre = "[", sep = ",", post = "]")(path)(list)
      assert(Files.readString(path) == "[1,2,3]")

   test("writeAll 3"):
      val path = makeFile()
      writeAll(path)(list)
      assert(Files.readString(path) == "1\n2\n3\n")

   test("writeAll 4"):
      val path = makeFile()
      writeAll(sep = "\n")(path)(list)
      assert(Files.readString(path) == "1\n2\n3")

   test("writeAll 5"):
      val path = makeFile()
      writeAll()(path)(list)
      assert(Files.readString(path) == "123")

   test("writeAll 6"):
      val path = makeFile()
      writeAll(path)(Nil)
      assert(Files.readString(path) == "")

   test("writeAll 7"):
      val path = makeFile()
      writeAll()(path)(Nil)
      assert(Files.readString(path) == "")

   test("writeAll 8"):
      val path = makeFile()
      writeAll(sep = ",")(path)(Nil)
      assert(Files.readString(path) == "")

   test("writeAll 9"):
      val path = makeFile()
      writeAll(pre = "[", sep = ",", post = "]")(path)(Nil)
      assert(Files.readString(path) == "[]")
