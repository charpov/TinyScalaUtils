package tinyscalautils.assertions

import scala.annotation.targetName

extension (left: Boolean)
   /** Logical implication. LHS is always evaluated. RHS is only evaluated if LHS side evaluated to
     * true.
     *
     * @since 1.1
     */
   inline infix def implies(inline right: Boolean): Boolean = !left || right

   /** Logical implication. LHS is always evaluated. RHS is only evaluated if LHS side evaluated to
     * true.
     *
     * Also available as `implies`
     *
     * @since 1.1
     */
   @targetName("implies")
   @deprecated("confusing precedence; use implies instead", since = "1.2")
   inline def ==> (inline right: Boolean): Boolean = !left || right
