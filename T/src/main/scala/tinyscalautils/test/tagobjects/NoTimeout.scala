package tinyscalautils.test.tagobjects

import org.scalatest.Tag

/** A "no timeout" tag. This tag can be used within `DualTimeLimits` to disable the timeout for a
  * single test. This overrides any other timeout tag, if present. Internally, `NoTimeout` uses a
  * `Span.Max` timeout.
  *
  * @see
  *   [[tinyscalautils.test.mixins.DualTimeLimits]]
  *
  * @see
  *   [[org.scalatest.time.Span]]
  *
  * @since 1.2
  */
object NoTimeout extends Tag("tinyscalautils.test.tags.NoTimeout")
