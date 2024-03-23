package tinyscalautils.lang

import tinyscalautils.assertions.checkNonNull

/** Stack overflows as exceptions instead of errors.
  *
  * @constructor
  *   Builds a new exception with the same message, cause and stack trace as the error. Note that
  *   `error` is _not_ used as cause.
  *
  * See [[tinyscalautils.control.noStackOverflow]]
  *
  * @throws IllegalArgumentException
  *   if error is null.
  *
  * @since 1.0
  */
class StackOverflowException(error: StackOverflowError)
    extends RuntimeException(checkNonNull(error).getMessage, error.getCause):
   setStackTrace(error.getStackTrace)
