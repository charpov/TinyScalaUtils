package tinyscalautils.text

import tinyscalautils.timing.now

/** Printing mode. Decides which of thread/time is shown and how.
  *
  * For instance,
  *
  * {{{
  * printf("(%d,%d)%n", x, y)
  * }}}
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
  * All printing modes in this package are stateless and thus safe to be shared among threads.
  *
  * @since 1.0
  */
trait PrintingMode:
   /** Formatted printing. */
   def print(arg: Any, newline: Boolean = false): Unit

   /** Formatted printing with a newline. */
   final def println(arg: Any): Unit = print(arg, newline = true)

   /** Formatted printing with a format. */
   final def printf(format: String, args: Any*): Unit = print(format.format(args*))
end PrintingMode

private def thread =
   val theThread = Thread.currentThread
   val name      = theThread.getName
   // replace getId with threadId in Java 19+
   if name.nonEmpty then name else "anonymous thread " + theThread.getId

/** Standard printing. Equivalent to `Predef.printf/println`. */
given standardMode: PrintingMode with
   def print(arg: Any, newline: Boolean): Unit =
      if newline then Predef.println(arg) else Predef.print(arg)

/** Silent. Does not print anything. No call to `Predef.printf/println` takes place. */
given silentMode: PrintingMode with
   def print(arg: Any, newline: Boolean): Unit = ()

/** Adds thread name. Strings are printed as: `<thread>: <string>`. */
given threadMode: PrintingMode with
   def print(arg: Any, newline: Boolean): Unit =
      val format = if newline then "%s: %s%n" else "%s: %s"
      Predef.printf(format, thread, arg)

/** Adds time. Strings are printed as: `at HH:MM:SS.millis: <string>` */
given timeMode: PrintingMode with
   def print(arg: Any, newline: Boolean): Unit =
      val format = if newline then "at %1$TT.%1$TL: %2$s%n" else "at %1$TT.%1$TL: %2$s"
      Predef.printf(format, now(), arg)

/** Adds time, but hides hours and minutes. Strings are printed as: `at XX:XX:SS.millis: <string>`
  */
given timeDemoMode: PrintingMode with
   def print(arg: Any, newline: Boolean): Unit =
      val format = if newline then "at XX:XX:%1$TS.%1$TL: %2$s%n" else "at XX:XX:%1$TS.%1$TL: %2$s"
      Predef.printf(format, now(), arg)

/** Adds thread name and time. Strings are printed as: `<thread> at HH:MM:SS.millis: <string>` */
given threadTimeMode: PrintingMode with
   def print(arg: Any, newline: Boolean): Unit =
      val format = if newline then "%1$s at %2$TT.%2$TL: %3$s%n" else "%1$s at %2$TT.%2$TL: %3$s"
      Predef.printf(format, thread, now(), arg)

/** Adds thread name and time, but hides hours and minutes. Strings are printed as:
  * `<thread> at XX:XX:SS.millis: <string>`
  */
given threadTimeDemoMode: PrintingMode with
   def print(arg: Any, newline: Boolean): Unit =
      val format =
         if newline then "%1$s at XX:XX:%2$TS.%2$TL: %3$s%n" else "%1$s at XX:XX:%2$TS.%2$TL: %3$s"
      Predef.printf(format, thread, now(), arg)
