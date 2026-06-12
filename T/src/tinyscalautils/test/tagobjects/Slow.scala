package tinyscalautils.test.tagobjects

import org.scalatest.Tag
import org.scalatest.tagobjects.Slow

@deprecated("use NoTag instead", since = "1.1")
object Fast extends Tag("tinyscalautils.test.tags.Fast"):
   @deprecated("use Slow.unless instead", since = "1.1")
   def when(condition: Boolean): Fast.type | Slow.type = if condition then Fast else Slow

extension (tag: Tag)
   /** A way to conditionally set a tag.
     *
     * @since 1.1
     */
   def when(condition: Boolean): tag.type | NoTag.type = if condition then tag else NoTag

   /** A way to conditionally set a tag.
     *
     * @since 1.1
     */
   def unless(condition: Boolean): tag.type | NoTag.type = if condition then NoTag else tag
