package tinyscalautils.test.text

import tinyscalautils.text.PrintingMode

import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import tinyscalautils.text.printout

/** A thread-safe "print mode" that stores its lines. */
class PrintAccumulator extends PrintingMode:
   private val lines = mutable.ArrayBuilder.make[String]

   def print(arg: Any, newline: Boolean = false): Unit =
      val str  = String.valueOf(arg)
      val line = if newline then str + System.lineSeparator() else str
      synchronized {
         lines += line
      }

   /** Retrieves all the lines and reset the accumulator for further usage. */
   def resetLines(): IndexedSeq[String] = synchronized {
      val array = lines.result()
      lines.clear()
      ArraySeq.unsafeWrapArray(array)
   }
end PrintAccumulator

private class PrintAccumulatorWithMode(mode: PrintingMode) extends PrintAccumulator:
   override def print(arg: Any, newline: Boolean = false): Unit =
      val str = printout(mode.print(arg, newline))
      super.print(str)

object PrintAccumulator:
   /** A thread-safe "print mode" that stores its lines. */
   def apply(): PrintAccumulator = new PrintAccumulator()

   /** A thread-safe "print mode" that stores its lines. After formatting the resulting string is
     * converted by the given underlying print mode before it is printed.
     */
   def apply(mode: PrintingMode): PrintAccumulator = PrintAccumulatorWithMode(mode)
