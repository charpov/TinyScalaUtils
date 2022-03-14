package tinyscalautils.text

/** Formatted printing with thread and time information.
  *
  * See [[println]] for details.
  *
  * @since 1.0
  */
def println(arg: Any)(using mode: PrintingMode): Unit = printf("%s%n", arg)

/** Formatted printing with thread and time information.
  *
  * or instance,
  *
  * {{{printf("(%d,%d)%n", x, y)}}}
  *
  * might produce output of the form: `T1 at 09:15:49.525: (2,3)` where `T1` is the name of the
  * thread that called `printf`.
  *
  * Actual format is controlled by a printing mode. For instance:
  *
  * {{{
  * import tinyscalautils.printing.{ println, threadTimeMode }
  *
  * println("message")
  * }}}
  *
  * might produce `main at 17:29:50.623: message`, while:
  *
  * {{{ import tinyscalautils.printing.{ println, threadMode }
  *
  * println("message") }}}
  *
  * might produce `main: message`.
  *
  * @see
  *   [[printf]]
  * @see
  *   [[PrintingMode]]
  *
  * @since 1.0
  */
def printf(format: String, args: Any*)(using mode: PrintingMode): Unit =
   mode.print(format.format(args*))
