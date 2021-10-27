import tinyscalautils.timing.SlowIterator

@main def slowIteratorDemo(): Unit =
   val i = Iterator.range(0, 1000)

   for (x <- i.slow(10.0, delayedElements = 10000)) do println(x)
