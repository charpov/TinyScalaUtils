package tinyscalautils.lang

import tinyscalautils.assertions.checkNonNull

/** Stack overflows as exceptions instead of errors.
  *
  * @constructor
  *   Builds a new exception with the same message, cause and stack trace as the error. Note that
  *   `error` is ''not'' used as cause.
  *
  * See [[tinyscalautils.control.noStackOverflow]]
  *
  * @since 1.0
  */
class StackOverflowException @throws[IllegalArgumentException]("if error is null") (
    error: StackOverflowError
) extends RuntimeException(checkNonNull(error).getMessage, error.getCause):
   setStackTrace(error.getStackTrace)
