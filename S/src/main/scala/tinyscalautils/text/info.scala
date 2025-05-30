package tinyscalautils.text

import tinyscalautils.assertions.require
import scala.util.Properties as Props

/** Prints basic runtime info.
  *
  * @throws IllegalArgumentException
  *   if `newLines` is negative.
  */
def info(newlines: Int = 0): Unit =
   require(newlines >= 0, s"number of newlines must be non-negative, not $newlines")
   val mem   = sys.runtime.maxMemory().toDouble / (1 << 30)
   val procs = tinyscalautils.threads.availableProcessors
   Predef.println(Props.javaVendor + " Java " + Props.javaVersion)
   Predef.println(Props.osName)
   Predef.println(s"""$procs ${plural(procs, "processor")}""")
   Predef.println(f"$mem%.1f GiB maximum memory")
   Predef.print(System.lineSeparator * newlines)
