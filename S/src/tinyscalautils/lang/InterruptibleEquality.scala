package tinyscalautils.lang

/** Checks for thread interrupts in `equals` and `hashCode`.
  *
  * This can be used to make code more responsive to interrupts, e.g.:
  *
  * {{{
  * class MyFancyClass extends SomeInterface with InterruptibleEquality:
  *   ...
  * }}}
  *
  * If a thread is interrupted and then attempts to call `equals` or `hashCode`, an
  * `InterruptedException` will be thrown; otherwise, the call is forwarded to the super method.
  *
  * Note that `==` and `##` rely on `equals` and `hasCode`, and thus will also throw the
  * `InterruptedException`.
  *
  * @since 1.0
  */
trait InterruptibleEquality:
   /** Checks for interrupts, then forwards call. */
   override def equals(obj: Any): Boolean =
      if Thread.interrupted() then throw InterruptedException() else super.equals(obj)

   /** Checks for interrupts, then forwards call. */
   override def hashCode(): Int =
      if Thread.interrupted() then throw InterruptedException() else super.hashCode()
