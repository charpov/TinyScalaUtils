package tinyscalautils.io

import java.io.{ Closeable, File, IOException, InputStream }
import java.net.URL
import java.nio.file.{ Files, Path }
import scala.collection.{ AbstractIterator, IterableFactory }
import scala.io.Codec.UTF8
import scala.io.{ Codec, Source }
import scala.util.Using

private lazy val nonEmpty = (str: String) => Option.unless(str.isBlank)(str)

/** Identity parsing. By using it as a `parser` argument of `readAll`, lines are returned
  * unchanged.
  */
lazy val noParsing: String => IterableOnce[String] = _ :: Nil

/** Text inputs, suitable to the `read` and `readAll` functions. */
@FunctionalInterface
trait Input[-I]:
   /** Opens the input as a source. */
   def source(in: I): Source

object Input:
   /** Summon. */
   def apply[I: Input]: Input[I] = summon

extension [I: Input](in: I)
   /** Opens the input as a source. */
   def source: Source = Input[I].source(in)

private given Codec = UTF8

given SourceIsInput: Input[Source]           = identity
given InputStreamIsInput: Input[InputStream] = Source.fromInputStream(_)
given FileIsInput: Input[File]               = Source.fromFile(_)
given URLIsInput: Input[URL]                 = Source.fromURL(_)
given FileNameIsInput: Input[String]         = Source.fromFile(_)
given PathIsInput: Input[Path]               = Files.newInputStream(_).source

/** Parses a text file (sequence of lines) using a given parser to split each line. Lines that parse
  * to an empty sequence (such as `None`) are ignored. Encoding is UTF8.
  *
  * @return
  *   a factory-based collection of all the parts of all the lines.
  *
  * @param in
  *   the source to parse.
  *
  * @param parser
  *   the parser to use; if omitted, each line is taken as a whole except blank lines, which are
  *   ignored.
  *
  * @param factory
  *   a factory for the desired collection type.
  *
  * @param silent
  *   if true, I/O errors (unreadable files, non-text files, ...) are ignored, and an empty
  *   collection is returned.
  *
  * @since 1.3
  */
def readAll[A, C[_], I: Input](
    factory: IterableFactory[C]
)(in: I, parser: String => IterableOnce[A] = nonEmpty, silent: Boolean = false): C[A] =
   try Using.resource(in.source)(_.getLines().flatMap(parser).to(factory))
   catch case e: IOException => if silent then factory.empty else throw e

/** Simplified form of `readAll` that uses `List` as the factory. */
def readAll[A, I: Input](in: I, parser: String => IterableOnce[A], silent: Boolean): List[A] =
   readAll(List)(in, parser, silent = silent)

/** Simplified form of `readAll` that uses `List` as the factory and `false` for the `silent`
  * argument.
  *
  * @since 1.3
  */
def readAll[A, I: Input](in: I, parser: String => IterableOnce[A]): List[A] =
   readAll(List)(in, parser, silent = false)

/** Simplified form of `readAll` that does not parse (blank lines are ignored) and uses `List` as
  * the factory.
  *
  * @since 1.3
  */
def readAll[I: Input](in: I, silent: Boolean): List[String] = readAll(List)(in, silent = silent)

/** Simplified form of `readAll` that does not parse (blank lines are ignored), uses `List` as the
  * factory, and uses `false` for the `silent` * argument.
  *
  * @since 1.3
  */
def readAll[I: Input](in: I): List[String] = readAll(List)(in, silent = false)

/** Reads a text file as a single string. Encoding is UTF8.
  *
  * @param in
  *   the source to read.
  *
  * @param silent
  *   if true, I/O errors (unreadable files, non-text files, ...) are ignored, and an empty string
  *   is returned.
  *
  * @since 1.3
  */
def read[I: Input](in: I, silent: Boolean = false): String =
   try Using.resource(in.source)(_.mkString)
   catch case e: IOException => if silent then "" else throw e

private class CloseableIterator[A](i: Iterator[A], closing: => Unit)
    extends AbstractIterator[A]
    with Closeable:
   export i.{ hasNext, next }

   def close(): Unit = closing
end CloseableIterator

/** Parses a text file (sequence of lines) using a given parser to split each line. Lines that parse
  * to an empty sequence (such as `None`) are ignored. Encoding is UTF8.
  *
  * @return
  *   an iterator of all the parts of all the lines; closing this iterator closes the underlying
  *   source.
  *
  * @param in
  *   the source to parse.
  *
  * @param parser
  *   the parser to use; if omitted, each line is taken as a whole except blank lines, which are
  *   ignored.
  *
  * @since 1.3
  */
def readingAll[A, I: Input](
    in: I,
    parser: String => IterableOnce[A] = nonEmpty
): Iterator[A] & Closeable =
   val source = in.source
   CloseableIterator(source.getLines().flatMap(parser), source.close())
