package tinyscalautils.text

import scala.compiletime.summonInline

/** Returns its input and prints a single dot, according to the given printing mode. The only
  * possible modes are `standardMode` (by default) and `silentMode`.
  */
inline def dot[A, M <: PrintingMode](any: A, theDot: Char = '.')(using mode: M = standardMode): A =
   summonInline[M <:< standardMode.type | silentMode.type]
   print(theDot)
   any

/** Returns its input and prints a single star, according to the given printing mode. The only
  * possible modes are `standardMode` (by default) and `silentMode`.
  */
def star[A](any: A)(using mode: PrintingMode = standardMode): A = dot(any, '*')
