package tinyscalautils.threads

import org.scalatest.funsuite.AnyFunSuite
import tinyscalautils.assertions.*

import java.util.concurrent.ThreadFactory

class KeepThreadsFactorySuite extends AnyFunSuite:

   private val noOp: Runnable = () => ()

   private class FactoryFromNames(names: Iterable[String]) extends ThreadFactory:
      private val nameIterator = names.iterator

      def newThread(r: Runnable): Thread =
         requireState(nameIterator.hasNext)
         Thread(r, nameIterator.next())
   end FactoryFromNames

   test("allThreads") {
      val tf             = KeepThreadsFactory(FactoryFromNames(Seq("A", "B", "C", "D")))
      val exec           = Executors.withFactory(tf).newThreadPool(4)
      val allThreadsView = tf.allThreads
      exec.execute(noOp)
      assert(allThreadsView.map(_.getName) == Seq("A"))
      exec.execute(noOp)
      assert(allThreadsView.map(_.getName) == Seq("A", "B"))
      val frozen = allThreadsView.toSeq
      tf.resetThreads()
      assert(allThreadsView.isEmpty)
      exec.execute(noOp)
      assert(allThreadsView.map(_.getName) == Seq("C"))
      exec.execute(noOp)
      assert(allThreadsView.map(_.getName) == Seq("C", "D"))
      assert(frozen.map(_.getName) == Seq("A", "B"))
      exec.shutdown()
   }
