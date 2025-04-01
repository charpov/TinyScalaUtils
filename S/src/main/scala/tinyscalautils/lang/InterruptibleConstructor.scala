package tinyscalautils.lang

/** Checks for thread interrupts at construction time.
  *
  * This can be mixed in to make code more responsive to interrupts:
  *
  * {{{
  * class MyFancyClass extends SomeInterface with InterruptibleConstructor:
  *   ...
  * }}}
  *
  * If a thread is interrupted and then attempts to create an instance of `MyFancyClass`, an
  * `InterruptedException` will be thrown.
  *
  * @since 1.0
  */
trait InterruptibleConstructor:
   if Thread.interrupted() then throw InterruptedException()
