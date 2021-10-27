package tinyscalautils.test.tagobjects

import org.scalatest.Tag

/**
  * A "not slow" tag.
  * This is so tests can be conditionally annotated with `Slow`.
  *
  * @see [[org.scalatest.tags.Slow]]
  * @since 1.5.0
  */
object Fast extends Tag("tinyscalautils.test.tags.Fast")
