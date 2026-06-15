package tinyscalautils.text

import tinyscalautils.assertions.*

/** Returns the singular form when `x` is less or equal to 1 and the plural form otherwise.
  *
  * @since 1.1
  */
def plural[A: Numeric as numeric](x: A, singularForm: String, pluralForm: String): String =
   if numeric.lteq(x, numeric.one) then singularForm else pluralForm

/** A variant of `plural` that guesses the plural form (e.g., cat -> cats, DOG -> DOGS, platypus ->
  * platypuses).
  *
  * @since 1.1
  */
def plural[A: Numeric](x: A, str: String): String =
   require(str.nonEmpty)
   val ext =
      str.last match
         case 's'            => "es"
         case 'S'            => "ES"
         case c if c.isUpper => "S"
         case _              => "s"
   plural(x, str, str + ext)
