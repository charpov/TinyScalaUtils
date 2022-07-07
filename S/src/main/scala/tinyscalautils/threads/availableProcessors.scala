package tinyscalautils.threads

/** The number of processors (cores) available, as reported by `Runtime.availableProcessors`. */
lazy val availableProcessors: Int = Runtime.getRuntime.availableProcessors()
