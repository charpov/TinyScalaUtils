package tinyscalautils.text
import tinyscalautils.assertions.require
import tinyscalautils.control.times

/** Basic runtime info. */
def info(newlines: Int = 0): Unit =
   require(newlines >= 0, s"invalid  number of newlines: $newlines")
   val mem   = Runtime.getRuntime.maxMemory().toDouble / (1 << 30)
   val procs = tinyscalautils.threads.availableProcessors
   Predef.println("Java " + Runtime.version())
   Predef.println(s"""$procs ${plural(procs, "processor")}""")
   Predef.println(f"$mem%.1f GiB maximum memory")
   newlines times Predef.println()
