package tinyscalautils.io

import java.io.{ File, OutputStream, OutputStreamWriter }
import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Path }
import scala.util.Using

/** Text outputs, suitable to the `write` and `writeAll` functions. */
@FunctionalInterface
trait Output[-O]:
   /** Opens the output as a stream. */
   def destination(out: O): OutputStream

object Output:
   /** Summon. */
   def apply[O: Output]: Output[O] = summon

extension [O: Output](out: O)
   /** Opens the output as a stream. */
   def destination: OutputStream = Output[O].destination(out)

private val UTF8 = StandardCharsets.UTF_8

given OutputStreamIsOutput: Output[OutputStream] = identity
given PathIsOutput: Output[Path]                 = Files.newOutputStream(_)
given FileNameIsOutput: Output[String]           = Path.of(_).destination
given FileIsOutput: Output[File]                 = _.toPath.destination

/** Writes the string into a destination. Nothing is added, unless when `newLine = true`, which adds
  * a final newline. Encoding is UTF8.
  *
  * @note
  *   Uses `CharSequence` instead of `Any` to avoid accidental calls to `write` that were intended
  *   to be `writeAll`.
  *
  * @note
  *   If the destination is closable, this function closes it.
  *
  * @param out
  *   the destination to write to.
  *
  * @param newline
  *   when true, adds a final newline to the output.
  */
def write[O: Output](out: O, newline: Boolean = false)(str: CharSequence): Unit =
   Using.resource(OutputStreamWriter(out.destination, UTF8)): writer =>
      writer.write(str.toString)
      if newline then writer.write('\n')

/** Writes the string representations of a collection of values into a destination. The three
  * arguments `pre`, `sep`, and `post` are as in `mkString`. Encoding is UTF8.
  *
  * @note
  *   If the destination is closable, this function closes it.
  *
  * @param out
  *   the destination to write to.
  */
def writeAll[A, O: Output](pre: String = "", sep: String = "", post: String = "")(
    out: O
)(values: IterableOnce[A]): Unit =
   Using.resource(OutputStreamWriter(out.destination, UTF8)): writer =>
      writer.write(pre)
      val i = values.iterator
      if i.nonEmpty then
         writer.write(i.next().toString)
         while i.hasNext do
            writer.write(sep)
            writer.write(i.next().toString)
      writer.write(post)

/** Writes the string representations of a collection of values into a destination. Each value is
  * followed by a newline, including the last.
  *
  * @note
  *   If the destination is closable, this function closes it.
  *
  * @param out
  *   the destination to write to.
  */
def writeAll[A, O: Output](out: O)(values: IterableOnce[A]): Unit =
   writeAll(sep = "\n")(out)(values.iterator ++ Iterator.single(""))
