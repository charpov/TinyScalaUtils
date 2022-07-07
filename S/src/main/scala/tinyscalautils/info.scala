package tinyscalautils

/** Basic runtime info. */
def info(): Unit =
   val mem = Runtime.getRuntime.maxMemory().toDouble / (1 << 30)
   println("Java " + Runtime.version())
   println(s"${threads.availableProcessors} processors")
   println(f"$mem%.1f GiB maximum memory")
