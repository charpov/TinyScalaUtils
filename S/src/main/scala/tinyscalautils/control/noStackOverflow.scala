package tinyscalautils.control

import tinyscalautils.lang.StackOverflowException

/** Captures [[java.lang.StackOverflowError]] and replaces with
  * [[tinyscalautils.lang.StackOverflowException]] in the given code.
  *
  * @since 1.1
  */
inline def noStackOverflow[A](inline code: A): A =
   try code
   catch case e: StackOverflowError => throw StackOverflowException(e)

@deprecated("renamed noStackOverflow", "1.1")
inline def limitedStack[A](inline code: A): A = noStackOverflow(code)
