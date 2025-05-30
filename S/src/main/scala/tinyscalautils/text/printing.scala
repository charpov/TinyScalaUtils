package tinyscalautils.text

/** Formatted printing.
  *
  * @since 1.0
  *
  * @see
  *   [[PrintingMode]]
  */
def print(arg: Any)(using mode: PrintingMode): Unit = mode.print(arg)

/** Formatted printing with a newline.
  *
  * @since 1.0
  *
  * @see
  *   [[PrintingMode]]
  */
def println(arg: Any)(using mode: PrintingMode): Unit = mode.print(arg, newline = true)

/** Formatted printing with a format.
  *
  * @since 1.0
  *
  * @see
  *   [[PrintingMode]]
  */
def printf(format: String, args: Any*)(using mode: PrintingMode): Unit = mode.printf(format, args*)
