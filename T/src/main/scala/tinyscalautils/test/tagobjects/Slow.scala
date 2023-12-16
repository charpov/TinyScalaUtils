package tinyscalautils.test.tagobjects

import org.scalatest.Tag
import org.scalatest.tagobjects.Slow

@deprecated("use NoTag instead", since = "1.1")
object Fast extends Tag("tinyscalautils.test.tags.Fast"):
   @deprecated("use Slow.unless instead", since = "1.1")
   inline def when(condition: Boolean): Fast.type | Slow.type = if condition then Fast else Slow

extension (slow: Slow.type)
   /** A way to conditionally tag as Slow.
     *
     * @since 1.1
     */
   inline def when(condition: Boolean): Slow.type | NoTag.type = if condition then slow else NoTag

   /** A way to conditionally tag as Slow.
     *
     * @since 1.1
     */
   inline def unless(condition: Boolean): Slow.type | NoTag.type = if condition then NoTag else slow
