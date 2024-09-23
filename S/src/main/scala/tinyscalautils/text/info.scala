package tinyscalautils.text

import tinyscalautils.assertions.require
import scala.util.Properties as Props

/** Basic runtime info. */
def info(newlines: Int = 0): Unit =
   require(newlines >= 0, s"invalid  number of newlines: $newlines")
   val mem   = sys.runtime.maxMemory().toDouble / (1 << 30)
   val procs = tinyscalautils.threads.availableProcessors
   Predef.println(Props.javaVendor + " Java " + Props.javaVersion)
   Predef.println(Props.osName)
   Predef.println(s"""$procs ${plural(procs, "processor")}""")
   Predef.println(f"$mem%.1f GiB maximum memory")
   Predef.print(System.lineSeparator * newlines)
