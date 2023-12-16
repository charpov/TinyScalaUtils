package tinyscalautils.text

import tinyscalautils.assertions.*

import scala.annotation.tailrec

/** Returns the singular form when `x` is less or equal to 1 and the plural form otherwise.
  *
  * @since 1.1
  */
inline def plural[A : Numeric](x: A, singularForm: String, pluralForm: String): String =
   val num = Numeric[A]
   if num.lteq(x, num.one) then singularForm else pluralForm

/** A variant of `plural` that guesses the plural form (e.g., cat -> cats, DOG -> DOGS, platypus ->
  * platypuses).
  *
  * @since 1.1
  */
def plural[A : Numeric](x: A, str: String): String =
   require(str.nonEmpty)
   val ext = str.last match
      case 's'            => "es"
      case 'S'            => "ES"
      case c if c.isUpper => "S"
      case _              => "s"
   plural(x, str, str + ext)

extension (str: String)
   /** String with all CRLF replaces with LF.
     *
     * @since 1.0
     */
   def cleanCRLF: String =
      @tailrec
      def clean(chars: List[Char], builder: StringBuilder): String = chars match
         case Nil               => builder.result()
         case '\r' :: '\n' :: r => clean(r, builder += '\n')
         case c :: r            => clean(r, builder += c)

      clean(str.toList, StringBuilder(str.length))
   end cleanCRLF

   /** Truncates a string to 32 characters.
     *
     * @since 1.0
     */
   def short: String = short(32)

   /** Truncates a string to a given length.
     *
     * @param maxLen
     *   the guaranteed maximum length of the string being returned.
     *
     * @since 1.0
     */
   def short(maxLen: Int): String =
      require(maxLen >= 3, s"limit $maxLen must be at least 3")
      if str.length > maxLen then str.substring(0, maxLen - 3) + "..." else str

   /** Pads a string to a given length.
     *
     * Contrary to `padTo`, this is a pad ''on the left''.
     *
     * @param minLen
     *   the guatanteed minimum length of the string being returned.
     *
     * @param padding
     *   the character used to pad.
     *
     * @since 1.0
     */
   def pad(minLen: Int, padding: Char = ' '): String =
      require(minLen >= 0, "lower limit cannot be negative")
      if str.length < minLen then padding.toString * (minLen - str.length) + str else str
