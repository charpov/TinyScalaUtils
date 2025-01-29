package tinyscalautils.control

private inline def check[A](inline code: A): A =
   if Thread.interrupted() then throw InterruptedException() else code

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
  * [[tinyscalautils.threads.runAsync]].
  *
  * @since 1.0
  */
@throws[InterruptedException]
inline def interruptibly[A](inline code: A): A = check(code)

given InterruptiblyExtensions: AnyRef with
   extension [I, O](f: I => O)
      /** @define text1
        *   Makes a function responsive to interrupts: `f.interruptibly` has the same behavior as
        *   `f` except that it checks for interrupts before each invocation. Additionally,
        *   `f.interruptibly` checks for interrupts once, before the resulting function is used.
        *
        * @note
        *   This is only implemented for functions of arity 1 and 2. For higher arities, one can use
        *   `untupled(f.tupled.interruptibly)`.
        *
        * @since 1.5
        *
        * $text1
        */
      @throws[InterruptedException]
      def interruptibly: I => O = check(x => check(f(x)))

   extension [I1, I2, O](f: (I1, I2) => O)
      /** $text1 */
      @throws[InterruptedException]
      def interruptibly: (I1, I2) => O = check((x, y) => check(f(x, y)))

   extension [I, O1, O2](h: (I => O1) => O2)(using DummyImplicit)
      /** @define text2
        *   Makes the function argument of a higher-order function `h` responsive to interrupts:
        *   `h.interruptiby(f)` is equivalent to `h(f.interruptibly)` but is more convenient when
        *   `f` is defined as a lambda expression, e.g.: `list.forall.interruptibly(x => x > 0)`.
        *
        * @note
        *   This handling of higher-order functions only works with arguments `f` or arity 1 and
        *   2. If `f` is a 3-argument function, for instance, `h.interruptibly(f)` is equivalent to
        *      `interruptibly(h(f))` instead. To achieved the desired behavior, one can use
        *      `h(untupled(f.tupled.interruptibly))`.
        *
        * @since 1.5
        *
        * $text2
        */
      @throws[InterruptedException]
      def interruptibly: (I => O1) => O2 = f => h(f.interruptibly)

   extension [I1, I2, O1, O2](h: ((I1, I2) => O1) => O2)(using d1: DummyImplicit, d2: DummyImplicit)
      /** $text2 */
      @throws[InterruptedException]
      def interruptibly: ((I1, I2) => O1) => O2 =
         f => h(f.interruptibly)
