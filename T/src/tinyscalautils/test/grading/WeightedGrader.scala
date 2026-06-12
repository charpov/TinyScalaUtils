package tinyscalautils.test.grading

import org.scalatest.*
import org.scalatest.events.*
import tinyscalautils.test.grading.WeightedGrader.weightRegex
import tinyscalautils.test.tagobjects.Points

import java.util.logging.Logger

/** A test reporter that counts passing and failing tests into a grade. */
private class WeightedGrader(defaultTotalWeight: Double) extends Grader, Reporter:
   private var sumWeight, sumPassed = 0.0
   private var tests                = 0
   private var points               = Map.empty[String, Seq[Double]].withDefaultValue(Seq())

   private def weightsOfName(name: String): Seq[Double] =
      weightRegex.findAllMatchIn(name).flatMap(_.group(1).toDoubleOption.filter(_ >= 0.0)).toSeq

   private def weightOfTag(tag: String): Option[Double] =
      tag match
         case s"$name($w)" if name == Points.name => w.toDoubleOption.filter(_ >= 0.0)
         case _                                   => None

   def setTags(map: Map[String, Set[String]]): Unit =
      points = Map
         .from(map.map((name, tags) => name -> tags.toSeq.flatMap(weightOfTag)))
         .withDefaultValue(Seq())

   private def weightOf(name: String): Double =
      points(name) ++ weightsOfName(name) match
         case Seq(w) => w
         case Seq()  => 1.0
         case _      =>
            Logger
               .getLogger("tinyscalautils")
               .warning(s"multiple point values set for test '$name'; all ignored")
            1.0

   /** The grade.  This is the weighted ratio of tests passed over tests failed. */
   def grade: Double = sumPassed / sumWeight

   /** Total weight.  By default, this is the sum of the weights of all the tests. */
   def totalWeight: Double = if defaultTotalWeight > 0.0 then defaultTotalWeight else sumWeight

   /** Total number of tests that were run.
     *
     * @since 1.1
     */
   def testCount: Int = tests

   /** Processes an event.
     *
     * `TestStarting`, `TestSucceeded` and `TestFailed` are used to keep track of the grade. Other
     * events are ignored.
     * @see
     *   [[org.scalatest.events.Event]]
     */
   def apply(event: Event): Unit =
      event match
         case e: TestSucceeded => recordTest(e.testName, successful = true)
         case e: TestFailed    => recordTest(e.testName, successful = false)
         case _                => () // do nothing

   private def recordTest(name: String, successful: Boolean) =
      val w = weightOf(name)
      tests += 1
      sumWeight += w
      if successful then sumPassed += w

end WeightedGrader

/** Companion object. */
private object WeightedGrader:
   private val weightRegex =
      val pointNames = "pt|pts|point|points|Pt|Pts|Point|Points"
      raw"""\[\s*(\d*\.?\d+)\s*($pointNames)\s*]""".r.unanchored
