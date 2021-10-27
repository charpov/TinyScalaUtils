package tinyscalautils.assertions

import scala.annotation.targetName

extension (left: Boolean)
   /** Logical implication. LHS is always evaluated. RHS is only evaluated if LHS side evaluated to
     * true.
     *
     * Also available as `==>`
     */
   inline infix def implies(inline right: Boolean): Boolean = !left || right

   /** Logical implication. LHS is always evaluated. RHS is only evaluated if LHS side evaluated to
     * true.
     *
     * Also available as `implies`
     */
   @targetName("implies")
   inline def ==> (inline right: Boolean): Boolean = !left || right
