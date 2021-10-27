package tinyscalautils.io

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{ Files, Path }
import scala.util.Using
import scala.jdk.StreamConverters.*
import tinyscalautils.assertions.*

import scala.collection.Factory

/** The list of files and subdirectories in a directory, in no particular order.
  *
  * @param silent
  *   if true, errors are ignored, and an empty list is returned.
  *
  * @since 1.0
  */
@throws[IllegalArgumentException]("if the path is not a directory")
def listPaths(dir: Path, silent: Boolean = false): List[Path] = readPaths(List)(dir, silent)

/** The collection of files and subdirectories in a directory, in no particular order.
  *
  * @param silent
  *   if true, errors are ignored, and an empty collection is returned.
  *
  * @param factory
  *   a factory for the desired collection type.
  *
  * @since 1.0
  */
def readPaths[C[_]](factory: Factory[Path, C[Path]])(dir: Path, silent: Boolean = false): C[Path] =
   require(Files.isDirectory(dir), s"$dir is not a directory")
   val contents = Using(Files.list(dir))(stream => stream.toScala(factory))
   if silent then contents.getOrElse(factory.fromSpecific(Seq.empty)) else contents.get

/** The list of lines in a text file, in UTF8 encoding.
  *
  * @param silent
  *   if true, errors (unreadable files, non-text files, ...) are ignored, and an empty list is
  *   returned.
  *
  * @since 1.0
  */
def listLines(file: Path, silent: Boolean = false): List[String] = readLines(List)(file, silent)

/** The collection of lines in a text file, in UTF8 encoding.
  *
  * @param silent
  *   if true, errors (unreadable files, non-text files, ...) are ignored, and an empty collection
  *   is returned.
  *
  * @param factory
  *   a factory for the desired collection type.
  *
  * @since 1.0
  */
def readLines[C[_]](
    factory: Factory[String, C[String]]
)(file: Path, silent: Boolean = false): C[String] =
   val lines = Using(Files.lines(file, UTF_8))(stream => stream.toScala(factory))
   if silent then lines.getOrElse(factory.fromSpecific(Seq.empty)) else lines.get
