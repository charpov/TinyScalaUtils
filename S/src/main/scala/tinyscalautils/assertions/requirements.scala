package tinyscalautils.assertions

/** Checks if an object is null.
  *
  * Throws [[java.lang.IllegalArgumentException]] if the object is `null`.
  *
  * @return
  *   the object itself.
  *
  * @since 1.0
  */
inline def checkNonNull[A <: AnyRef](obj: A): obj.type =
   if obj eq null then throw IllegalArgumentException("no nulls") else obj

/** Simple argument assertion, with message.
  *
  * Throws [[java.lang.IllegalArgumentException]] if the requirement is not satisfied. Messages are
  * created lazily, either as:
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
@throws[IllegalArgumentException]("if the requirement is not satisfied")
inline def require(condition: Boolean, inline message: String, inline args: Any*): Unit =
   if !condition then throw IllegalArgumentException(message.format(args*))

/** Simple argument assertion.
  *
  * Throws [[java.lang.IllegalArgumentException]] if the requirement is not satisfied. Message is
  * `null`.
  *
  * @since 1.0
  */
@throws[IllegalArgumentException]("if the requirement is not satisfied")
inline def require(condition: Boolean): Unit =
   if !condition then throw IllegalArgumentException()

/** Simple state assertion, with message.
  *
  * Throws [[java.lang.IllegalStateException]] if the requirement is not satisfied. Messages are
  * created lazily, either as:
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
@throws[IllegalStateException]("if the requirement is not satisfied")
inline def requireState(condition: Boolean, inline message: String, inline args: Any*): Unit =
   if !condition then throw IllegalStateException(message.format(args*))

/** Simple state assertion.
  *
  * Throws [[java.lang.IllegalStateException]] if the requirement is not satisfied. Message is
  * `null`.
  *
  * @since 1.0
  */
@throws[IllegalStateException]("if the requirement is not satisfied")
inline def requireState(condition: Boolean): Unit = if !condition then throw IllegalStateException()
