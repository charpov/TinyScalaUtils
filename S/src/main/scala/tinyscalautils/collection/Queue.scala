package tinyscalautils.collection

import java.util

extension [A](queue: util.Queue[A])
   /** Like `poll` but wrapping `null` in an option.
    *
    * @since 1.2
    */
   def pollOption(): Option[A] = Option(queue.poll())

   /** Like `peek` but wrapping `null` in an option.
    *
    * @since 1.2
    */
   def peekOption: Option[A] = Option(queue.peek())
