package tinyscalautils.io

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{ Files, Path }
import scala.util.Using
import scala.jdk.StreamConverters.*
import tinyscalautils.assertions.*

/** The list of files and subdirectories in a directory, in no particular order.
  *
  * @param silent
  *   if true, errors are ignored, and an empty list is returned.
  *
  * @since 1.0
  */
@throws[IllegalArgumentException]("if the path is not a directory")
def listPaths(dir: Path, silent: Boolean = false): List[Path] =
   require(Files.isDirectory(dir), s"$dir is not a directory")
   val contents = Using(Files.list(dir))(stream => stream.toScala(List))
   if silent then contents.getOrElse(List.empty) else contents.get

/** The list of lines in a text file, in UTF8 encoding.
  *
  * @param silent
  *   if true, errors (unreadable files, non-text files, ...) are ignored, and an empty list is
  *   returned.
  *
  * @since 1.0
  */
def listLines(file: Path, silent: Boolean = false): List[String] =
   val lines = Using(Files.lines(file, UTF_8))(stream => stream.toScala(List))
   if silent then lines.getOrElse(List.empty) else lines.get
