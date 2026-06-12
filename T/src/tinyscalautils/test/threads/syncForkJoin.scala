package tinyscalautils.test.threads

import java.util.concurrent.{ CyclicBarrier, Executor }
import tinyscalautils.threads.Run
import tinyscalautils.timing.timeOf

def syncForkJoin[A](inputs: Iterable[A], action: => Any = ())(code: A => Any)(
    using Executor
): Double =
   val barrier = CyclicBarrier(inputs.size + 1)
   for input <- inputs do
      Run:
         barrier.await()
         code(input)
         barrier.await()
   barrier.await()
   timeOf:
      action
      barrier.await()
