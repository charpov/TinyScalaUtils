package tinyscalautils.control

/** Checks for interrupts before running code.
  *
  * This can be used to make loops more responsive to interrupts, e.g.:
  *
  * {{{
  * while condition do interruptibly {
  *     ...
  *   }
  * }}}
  *
  * Note that the body of the loop can remain unresponsive. To deal with that, use
  * [[package.stoppably]].
  *
  * @since 1.0
  */
@throws[InterruptedException]
inline def interruptibly[A](inline code: A): A =
   if Thread.interrupted() then throw InterruptedException() else code
