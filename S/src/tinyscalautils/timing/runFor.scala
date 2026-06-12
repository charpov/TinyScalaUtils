package tinyscalautils.timing

import tinyscalautils.lang.unit
import tinyscalautils.threads.Timer
import scala.util.chaining.scalaUtilChainingOps
import scala.annotation.{ tailrec, targetName }

/** Repeated invocations of `step` with a time bound. The function is repeatedly invoked on its
  * previous output until it returns `None` or the timeout has been reached.
  *
  * @note
  *   The stepping function is run in the thread that invokes `runFor`. No attempt is made to
  *   interrupt the thread inside `step`.
  *
  * @param seconds
  *   a timeout in seconds; if it is negative, `step` is not invoked at all; it it is zero, `step`
  *   is invoked exactly once.
  *
  * @param start
  *   an initial value on which to execute `step`.
  *
  * @param step
  *   the stepping function
  *
  * @return
  *   a flag that indicates normal termination (no timeout).
  *
  * @since 1.2
  */
def runFor[A](seconds: Double)(start: A)(step: A => Option[A])(using timer: Timer): Boolean =
   if seconds < 0.0 then false
   else if seconds == 0.0 then step(start).isEmpty
   else
      @volatile var continue = true
      timer.schedule(seconds)({ continue = false })

      @tailrec
      def loop(st: A): Boolean = step(st) match
         case None       => true
         case Some(next) => continue && loop(next)
      loop(start)
end runFor

/** Repeated invocations of `step` with a time bound. The function is repeatedly invoked for side
  * effects until it returns `false` or the timeout has been reached.
  *
  * @note
  *   The stepping function is run in the thread that invokes `runFor`. No attempt is made to
  *   interrupt the thread inside `step`.
  *
  * @param seconds
  *   a timeout in seconds; if it is negative, `step` is not invoked at all; it it is zero, `step`
  *   is invoked exactly once.
  *
  * @param step
  *   the stepping function
  *
  * @return
  *   a flag that indicates normal termination (no timeout).
  *
  * @since 1.2
  */
def runFor(seconds: Double)(step: () => Boolean)(using Timer): Boolean =
   runFor(seconds)(unit)(_ => Option.when(step())(unit))

/** Repeated invocations of `step` with a time bound. The function is repeatedly invoked on the
  * "state" part of its previous output until it returns `None` or the timeout has been reached. The
  * "value" part of the pair is collected into a sequence.
  *
  * @note
  *   The stepping function is run in the thread that invokes `callFor`. No attempt is made to
  *   interrupt the thread inside `step`.
  *
  * @param seconds
  *   a timeout in seconds; if it is negative, `step` is not invoked at all; it it is zero, `step`
  *   is invoked exactly once.
  *
  * @param start
  *   an initial state on which to execute `step`.
  *
  * @param step
  *   the stepping function
  *
  * @return
  *   a pair: a sequence of values produced by `step` and a flag that indicates normal termination
  *   (no timeout).
  *
  * @since 1.2
  */
@targetName("callForOption")
def callFor[A, S](seconds: Double)(start: S)(step: S => Option[(A, S)])(
    using Timer
): (Seq[A], Boolean) =
   val builder = Seq.newBuilder[A]
   val timedOut = runFor(seconds)((null.asInstanceOf[A], start)): (_, state) =>
      step(state).tap(_.foreach((value, _) => builder += value))
   (builder.result(), timedOut)

/** Repeated invocations of `step` with a time bound. The function is repeatedly invoked on the
  * "state" part of its previous output until it returns `None` or the timeout has been reached. The
  * "value" part of the pair is collected into a sequence.
  *
  * @note
  *   The stepping function is run in the thread that invokes `callFor`. No attempt is made to
  *   interrupt the thread inside `step`.
  *
  * @param seconds
  *   a timeout in seconds; if it is negative, `step` is not invoked at all; it it is zero, `step`
  *   is invoked exactly once.
  *
  * @param start
  *   an initial state on which to execute `step`.
  *
  * @param step
  *   the stepping function
  *
  * @return
  *   a pair: a sequence of values produced by `step` and a flag that indicates normal termination
  *   (no timeout).
  *
  * @since 1.2
  */
@targetName("callForPair")
def callFor[A, S](seconds: Double)(start: S)(step: S => (A, Option[S]))(
    using Timer
): (Seq[A], Boolean) =
   val builder = Seq.newBuilder[A]
   val timedOut = runFor(seconds)((null.asInstanceOf[A], start)): (_, state) =>
      val (value, next) = step(state)
      builder += value
      for st <- next yield value -> st
   (builder.result(), timedOut)

/** Repeated invocations of `step` with a time bound. The function is repeatedly invoked on its
  * previous output until it returns `None` or the timeout has been reached. The returned values are
  * collected into a sequence.
  *
  * @note
  *   The stepping function is run in the thread that invokes `callFor`. No attempt is made to
  *   interrupt the thread inside `step`.
  *
  * @param seconds
  *   a timeout in seconds; if it is negative, `step` is not invoked at all; it it is zero, `step`
  *   is invoked exactly once.
  *
  * @param start
  *   an initial state on which to execute `step`.
  *
  * @param step
  *   the stepping function
  *
  * @return
  *   a pair: a sequence of values produced by `step` and a flag that indicates normal termination
  *   (no timeout).
  *
  * @since 1.2
  */
def callFor[A](seconds: Double)(start: A)(step: A => Option[A])(using Timer): (Seq[A], Boolean) =
   callFor[A, A](seconds)(start)(st => step(st).map(x => (x, x)))
