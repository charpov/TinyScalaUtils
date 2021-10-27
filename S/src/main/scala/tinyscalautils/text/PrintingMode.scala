package tinyscalautils.text

/** Printing mode. Decides which of thread/time is shown and how.
  *
  * All printing modes in this package are stateless, and thus safe to be shared among threads.
  *
  * @since 1.0
  */
trait PrintingMode:
   private[text] def print(str: String): Unit

private inline def thread = Thread.currentThread.getName

private inline def now = System.currentTimeMillis()

private object StandardMode extends PrintingMode:
   def print(str: String) = Predef.print(str)

private object SilentMode extends PrintingMode:
   def print(str: String) = ()

private object ThreadMode extends PrintingMode:
   def print(str: String) = Predef.printf("%s: %s", thread, str)

private object TimeMode extends PrintingMode:
   def print(str: String) = Predef.printf("at %1$TT.%1$TL: %2$s", now, str)

private object TimeDemoMode extends PrintingMode:
   def print(str: String) = Predef.printf("at XX:XX:%1$TS.%1$TL: %2$s", now, str)

private object ThreadTimeMode extends PrintingMode:
   def print(str: String) = Predef.printf("%1$s at %2$TT.%2$TL: %3$s", thread, now, str)

private object ThreadTimeDemoMode extends PrintingMode:
   def print(str: String) = Predef.printf("%1$s at XX:XX:%2$TS.%2$TL: %3$s", thread, now, str)

/** Standard printing. Equivalent to `Predef.printf/println`. */
given standardMode: PrintingMode = StandardMode

/** Silent. Does not print anything. No call to `Predef.printf/println` takes place. */
given silentMode: PrintingMode = SilentMode

/** Adds thread name. Strings are printed as: `<thread>: <string>`. */
given threadMode: PrintingMode = ThreadMode

/** Adds time. Strings are printed as: `at HH:MM:SS.millis: <string>` */
given timeMode: PrintingMode = TimeMode

/** Adds time, but hides hours and minutes. Strings are printed as: `at XX:XX:SS.millis: <string>`
  */
given timeDemoMode: PrintingMode = TimeDemoMode

/** Adds thread name and time. Strings are printed as: `<thread> at HH:MM:SS.millis: <string>` */
given threadTimeMode: PrintingMode = ThreadTimeMode

/** Adds thread name and time, but hides hours and minutes. Strings are printed as: `<thread> at
  * XX:XX:SS.millis: <string>`
  */
given threadTimeDemoMode: PrintingMode = ThreadTimeDemoMode
