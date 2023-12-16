package tinyscalautils.util

extension (n: Int)
   /** True iff an integer is even. */
   def isEven: Boolean = (n & 1) == 0

   /** True iff an integer is odd. */
   def isOdd: Boolean  = (n & 1) == 1

extension (n: Long)
   /** True iff an integer is even. */
   def isEven: Boolean = (n & 1L) == 0L

   /** True iff an integer is odd. */
   def isOdd: Boolean  = (n & 1L) == 1L
