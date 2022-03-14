package tinyscalautils.text

import java.io.{ ByteArrayOutputStream, PrintStream }
import java.nio.charset.Charset

/** Captures the output of print statements into a string.
  *
  * This captures `Console.out`. `Console.err` is let through by default, but can be included in the
  * capture by setting `includeErr` to true. Also left out are `System.out` and `System.err`, for
  * which there is also an option, `includeSystem`. Both options can be combined for four possible
  * combinations. (It is not possible to capture `System.out/err` without capturing
  * `Console.out/err`.)
  *
  * The mechanism used to capture `Console.out/err` is thread-safe. In particular, different threads
  * can capture independently. `System.out/err`, on the other hand, are global variables.
  *
  * @see
  *   [[System.setOut]]
  * @see
  *   [[Console.withOut]]
  *
  * @since 1.0
  */
def printout[A](
    includeErr: Boolean = false,
    includeSystem: Boolean = false,
    charset: Charset = Charset.defaultCharset()
)(
    code: => A
): String = (if includeSystem then printoutWithSystem else printoutSimple)(code, includeErr, charset)

/** Captures the output of print statements into a string.
  *
  * This is the short form of `printout` that uses default values.
  *
  * @since 1.0
  */
def printout[A](code: => A): String = printout()(code)

private def printoutSimple[A](code: => A, includeErr: Boolean, charset: Charset) =
   val buffer = ByteArrayOutputStream()
   Console.withOut(buffer)(if includeErr then Console.withErr(buffer)(code) else code)
   buffer.toString(charset)

private def printoutWithSystem[A](code: => A, includeErr: Boolean, charset: Charset) =
   val buffer = ByteArrayOutputStream()
   val stream = PrintStream(buffer)
   val out    = System.out
   val err    = System.err
   try
      System.setOut(stream)
      if includeErr then System.setErr(stream)
      Console.withOut(stream)(if includeErr then Console.withErr(stream)(code) else code)
      buffer.toString(charset)
   finally
      System.setOut(out)
      if includeErr then System.setErr(err)
