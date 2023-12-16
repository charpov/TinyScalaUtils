package tinyscalautils.test.mixins

import java.util.concurrent.ThreadFactory

import org.scalatest.{ Outcome, TestSuite, TestSuiteMixin }

@deprecated("use runAsync or Async tag", since = "1.1")
trait RunnerFactory extends TestSuiteMixin:
   self: TestSuite =>

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
