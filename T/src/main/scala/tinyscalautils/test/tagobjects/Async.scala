package tinyscalautils.test.tagobjects

import org.scalatest.Tag

/** This tag indicates that a test should run in a separate (interruptible) thread, taken from
  * `global`. It is used in conjunction with the `GradingRun` trait to allow the remaining tests to
  * start running after a test times out but fails to terminate.
  *
  * @note
  *   This is used in scenarios where the actual testing code does not respond to interrupts. It
  *   will continue to run after a test times out, concurrently with the following tests. Failed
  *   tests blocked on I/O may not be an issue, but computing tests will continue to use resources
  *   while other tests run.
  *
  * @see
  *   [[tinyscalautils.threads.Executors.global]]
  *
  * @see
  *   [[tinyscalautils.threads.runAsync]]
  *
  * @see
  *   [[tinyscalautils.test.grading.GradingRun]]
  *
  * @since 1.1
  */
object Async extends Tag("tinyscalautils.test.tags.Async")
