package tinyscalautils.text

/** Basic runtime info. */
def info(): Unit =
   val mem = Runtime.getRuntime.maxMemory().toDouble / (1 << 30)
   Predef.println("Java " + Runtime.version())
   Predef.println(s"${tinyscalautils.threads.availableProcessors} processors")
   Predef.println(f"$mem%.1f GiB maximum memory")
