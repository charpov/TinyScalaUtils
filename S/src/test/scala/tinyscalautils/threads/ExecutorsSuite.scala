package tinyscalautils.threads

import org.scalactic.Tolerance
import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.control.times
import tinyscalautils.lang.unit
import tinyscalautils.threads.shutdownAndWait
import tinyscalautils.timing.timeOf

import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{ Callable, ConcurrentLinkedQueue, RejectedExecutionException }
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContext, Future }

class ExecutorsSuite extends AnyFunSuite with Tolerance:
   private val noOp: Runnable = () => ()

   test("default"):
      val exec1 = Executors.newThreadPool(4)
      val exec2 = Executors.newUnlimitedThreadPool()
      exec1.shutdown()
      exec2.shutdown()
      assertThrows[RejectedExecutionException](exec1.execute(noOp))
      assertThrows[RejectedExecutionException](exec2.execute(noOp))

   test("rejection policy"):
      val exec1 = Executors.withRejectionPolicy(DiscardPolicy()).newThreadPool(4)
      val exec2 = Executors.withRejectionPolicy(DiscardPolicy()).newUnlimitedThreadPool()
      exec1.shutdown()
      exec2.shutdown()
      exec1.execute(noOp)
      exec2.execute(noOp)

   test("silent"):
      val exec1 = Executors.silent.newThreadPool(4)
      val exec2 = Executors.silent.newUnlimitedThreadPool()
      exec1.shutdown()
      exec2.shutdown()
      exec1.execute(noOp)
      exec2.execute(noOp)

   test("factory"):
      val tf1, tf2 = KeepThreadsFactory()
      val exec1    = Executors.withFactory(tf1).newThreadPool(2)
      val exec2    = Executors.withFactory(tf2).newUnlimitedThreadPool()
      10 times:
         exec1.execute(noOp)
         exec2.execute(noOp)
      exec1.shutdownAndWait(1.0)
      exec2.shutdownAndWait(1.0)
      assert(tf1.allThreads.size == 2)
      assert(tf2.allThreads.nonEmpty && tf2.allThreads.size <= 10)

   test("rejection policy and factory"):
      val tf1, tf2 = KeepThreadsFactory()
      val exec1 = Executors
         .withRejectionPolicy(DiscardPolicy())
         .withFactory(tf1)
         .newThreadPool(2)
      val exec2 = Executors
         .withRejectionPolicy(DiscardPolicy())
         .withFactory(tf2)
         .newUnlimitedThreadPool()
      10 times:
         exec1.execute(noOp)
         exec2.execute(noOp)
      exec1.shutdownAndWait(1.0)
      exec2.shutdownAndWait(1.0)
      assert(tf1.allThreads.size == 2)
      assert(tf2.allThreads.nonEmpty && tf2.allThreads.size <= 10)
      exec1.execute(noOp)
      exec2.execute(noOp)

   test("factory and rejection policy"):
      val tf1, tf2 = KeepThreadsFactory()
      val exec1 = Executors
         .withFactory(tf1)
         .withRejectionPolicy(DiscardPolicy())
         .newThreadPool(2)
      val exec2 = Executors
         .withFactory(tf2)
         .withRejectionPolicy(DiscardPolicy())
         .newUnlimitedThreadPool()
      10 times:
         exec1.execute(noOp)
         exec2.execute(noOp)
      exec1.shutdownAndWait(1.0)
      exec2.shutdownAndWait(1.0)
      assert(tf1.allThreads.size == 2)
      assert(tf2.allThreads.nonEmpty && tf2.allThreads.size <= 10)
      exec1.execute(noOp)
      exec2.execute(noOp)

   test("run 1"):
      val main   = Thread.currentThread
      val runner = AtomicReference[Thread]()
      val exec   = Executors.newUnlimitedThreadPool()
      exec.run(runner.set(Thread.currentThread))
      exec.shutdownAndWait()
      assert(runner.get != main)

   test("run 2"):
      val main   = Thread.currentThread
      val runner = AtomicReference[Thread]()
      withThreads(awaitTermination = true):
         Run(runner.set(Thread.currentThread))
      assert(runner.get != main)

   test("run 3"):
      val task: Runnable = () => unit
      Executors.global.run(task: AnyRef)
      assertDoesNotCompile("Executors.global.run(task)")

   test("run 4"):
      val task: Callable[String] = () => ""
      Executors.global.run(task: AnyRef)
      assertDoesNotCompile("Executors.global.run(task)")

   test("keepAlive 1"):
      val exec    = Executors.newUnlimitedThreadPool(2.0)
      val threads = ConcurrentLinkedQueue[Thread]()
      10 times exec.run(threads.add(Thread.currentThread))
      val time = timeOf(threads.forEach(_.join()))
      exec.shutdown()
      assert(time === 2.0 +- 0.1)

   test("keepAlive 2"):
      val exec    = Executors.newThreadPool(16, keepAlive = 2.0)
      val threads = ConcurrentLinkedQueue[Thread]()
      10 times exec.run(threads.add(Thread.currentThread))
      val time = timeOf(threads.forEach(_.join()))
      exec.shutdown()
      assert(time === 2.0 +- 0.1)

   test("global"):
      given ExecutionContext = tinyscalautils.threads.Executors.global

      val f = Future(42)
      assert(Await.result(f, Duration.Inf) == 42)
