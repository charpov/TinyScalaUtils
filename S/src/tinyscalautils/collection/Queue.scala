package tinyscalautils.collection

import java.util

extension [A](queue: util.Queue[A])
   @deprecated("use Option instead", since = "1.3")
   def pollOption(): Option[A] = Option(queue.poll())

   @deprecated("use Option instead", since = "1.3")
   def peekOption: Option[A] = Option(queue.peek())
