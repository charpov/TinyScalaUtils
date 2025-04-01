package tinyscalautils.assertions

/** Checks if an object is null.
  *
  * @throws IllegalArgumentException
  *   if the object is `null`.
  *
  * @return
  *   the object itself.
  *
  * @since 1.0
  */
inline def checkNonNull(obj: AnyRef): obj.type =
   if obj eq null then throw IllegalArgumentException("no nulls") else obj

/** Simple argument assertion, with message.
  *
  * @throws IllegalArgumentException
  *   if the requirement is not satisfied.
  *
  * Messages are created lazily, either as:
  *
  * {{{require(cond, s"$variable should be $value")}}}
  *
  * or:
  *
  * {{{require(cond, "%s should be %s", variable, value)}}}
  *
  * The message string cannot be `null`.
  *
  * @since 1.0
  */
inline def require(condition: Boolean, inline message: String, inline args: Any*): Unit =
   if !condition then throw IllegalArgumentException(message.format(args*))

/** Simple argument assertion.
  *
  * @throws IllegalArgumentException
  *   if the requirement is not satisfied.
  *
  * Message in the exception is `null`.
  *
  * @since 1.0
  */
inline def require(condition: Boolean): Unit =
   if !condition then throw IllegalArgumentException()

/** Simple state assertion, with message.
  *
  * @throws IllegalStateException
  *   if the requirement is not satisfied.
  *
  * Messages are created lazily, either as:
  *
  * {{{require(cond, s"$variable should be $value")}}}
  *
  * or:
  *
  * {{{require(cond, "%s should be %s", variable, value)}}}
  *
  * The message string cannot be `null`.
  *
  * @since 1.0
  */
inline def requireState(condition: Boolean, inline message: String, inline args: Any*): Unit =
   if !condition then throw IllegalStateException(message.format(args*))

/** Simple state assertion.
  *
  * @throws IllegalStateException
  *   if the requirement is not satisfied.
  *
  * Message in the exception is `null`.
  *
  * @since 1.0
  */
inline def requireState(condition: Boolean): Unit = if !condition then throw IllegalStateException()
