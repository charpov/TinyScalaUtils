package tinyscalautils.control

import scala.annotation.unused

extension [A](left: A)
   /** An SML-like operator that evaluates two arguments and returns the value of the first one.
     * This is simply a mechanism to avoid naming an intermediate result: `x before y` is strictly
     * equivalent to `{val r=x; y; r}`.
     *
     * @since 1.5.0
     */
   inline infix def before[U](@unused right: U): A = left
