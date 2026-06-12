package tinyscalautils.text

import tinyscalautils.assertions.*

import scala.annotation.tailrec

extension (str: String)
   /** String with all CRLF replaces with LF.
     *
     * @since 1.0
     */
   def cleanCRLF: String =
      @tailrec
      def clean(chars: List[Char], builder: StringBuilder): String =
         chars match
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
     * @throws IllegalArgumentException
     *   if `maxLen` is less than 3.
     *
     * @since 1.0
     */
   def short(maxLen: Int): String =
      require(maxLen >= 3, s"limit must be at least 3, not $maxLen")
      if str.length > maxLen then str.substring(0, maxLen - 3) + "..." else str

   /** Pads a string to a given length.
     *
     * Contrary to `padTo`, this is a pad _on the left_.
     *
     * @param minLen
     *   the guatanteed minimum length of the string being returned.
     *
     * @param padding
     *   the character used to pad.
     *
     * @throws IllegalArgumentException
     *   if `minLen` is negative.
     *
     * @since 1.0
     */
   def pad(minLen: Int, padding: Char = ' '): String =
      require(minLen >= 0, s"lower limit must be non-negative, not $minLen")
      if str.length < minLen then padding.toString * (minLen - str.length) + str else str
