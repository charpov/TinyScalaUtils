package tinyscalautils.test.mixins

import java.util.concurrent.ThreadFactory

import org.scalatest.{ Outcome, TestSuite, TestSuiteMixin }

/** Runs each test in a new thread. The threads are created by the `runnerFactory`.
  *
  * The ScalaTest thread creates and starts a new thread to run the test. It then waits for this
  * thread to terminate. If the test throws an exception, it is rethrown. If the ScalaTest thread is
  * interrupted while waiting, it interrupts the working thread and throws `InterruptedException`.
  *
  * The primary purpose of this mechanism is to use stoppable threads to run tests (@see
  * [[tinyscalautils.threads.StoppableThread]]). For this reason, the working thread only runs the
  * test code, not any of the fixtures, which are run by the ScalaTest thread, and can be stacked as
  * usual. It would be dangerous to forcibly stop a thread that is running fixtures and other
  * ScalaTest code.
  *
  * @since 1.0
  */
trait RunnerFactory extends TestSuiteMixin:
   self: TestSuite =>

   /** The factory used to create the threads that run actual test code. */
   val runnerFactory: ThreadFactory

   abstract override def withFixture(test: NoArgTest): Outcome =
      val newTest =
         new NoArgTest:
            def apply(): Outcome =
               var outcome: Outcome = null
               var ex: Throwable    = null
               val runner = runnerFactory.newThread { () =>
                  try outcome = test()
                  catch case e: Throwable => ex = e
               }
               runner.start()
               try runner.join()
               catch
                  case e: InterruptedException =>
                     runner.interrupt()
                     throw e
               if ex ne null then throw ex
               Predef.assert(outcome != null, "thread factory error: runner thread did not run test")
               outcome
            end apply

            val configMap = test.configMap
            val name      = test.name
            val scopes    = test.scopes
            val text      = test.text
            val tags      = test.tags
            val pos       = test.pos
      super.withFixture(newTest)
