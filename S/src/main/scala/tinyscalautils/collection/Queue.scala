package tinyscalautils.collection

import java.util
import scala.annotation.experimental

extension [A](queue: util.Queue[A])
   @experimental
   def pollOption(): Option[A] = Option(queue.poll())

   @experimental
   def peekOption: Option[A] = Option(queue.peek())
