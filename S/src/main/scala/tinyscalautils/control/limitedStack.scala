package tinyscalautils.control

import tinyscalautils.lang.StackOverflowException

/** Captures [[java.lang.StackOverflowError]] and replaces with
  * [[tinyscalautils.lang.StackOverflowException]] in the given code.
  *
  * @since 1.0
  */
def limitedStack[A](code: => A): A =
   try code
   catch case e: StackOverflowError => throw StackOverflowException(e)
