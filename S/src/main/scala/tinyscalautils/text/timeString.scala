package tinyscalautils.text

import tinyscalautils.assertions.require

private val names: Seq[String] = Seq("day", "hour", "minute", "second", "millisecond")
private val maxSeconds: Double = java.lang.Double.valueOf("0x1.517ffffea033p47")

/** Human-friendly representation of a duration, using days, hours, minutes, seconds and
  * milliseconds.
  *
  * @param unitsCount
  *   the number of different units used in the representation, between 1 and 5. Using 2 units (the
  *   defaults) results in strings of the form `3 minutes, 20 milliseconds` or `1 hour, 10 seconds`.
  *
  * @throws IllegalArgumentException
  *   if the duration is negative, the number of units is less than 1, or the duration exceeds
  *   2147483647 days.
  *
  * @since 1.1
  */
def timeString(seconds: Double, unitsCount: Int = 2): String =
   require(seconds >= 0.0, s"duration must be non-negative, not $seconds")
   require(unitsCount > 0, s"unit count must be positive, not $unitsCount")
   require(seconds < maxSeconds, s"no more than ${Int.MaxValue} days")

   if seconds < 0.0005 then "0 second"
   else
      val durations =
         val millis = (seconds * 1000.0).round
         val d      = (millis / 86_400_000).toInt
         var ms     = (millis % 86_400_000).toInt
         val h      = ms / 3_600_000
         ms %= 3_600_000
         val m = ms / 60_000
         ms %= 60_000
         val s = ms / 1_000
         ms %= 1_000
         List(d -> -1, h -> 24, m -> 60, s -> 60, ms -> 1000)

      // rounding up times based on number of units used
      def round(times: List[(Int, Int)], u: Int): (List[Int], Boolean) =
         times match
            case Nil => (Nil, false)
            case (time, max) :: tail =>
               val (list, carry) = round(tail, if time == 0 then u else u - 1)
               val duration      = if carry then time + 1 else time
               if u <= 0 then (0 :: list, duration >= max / 2)
               else if duration == max then (0 :: list, true)
               else (duration :: list, false)

      val str = StringBuilder()
      for (duration, name) <- round(durations, unitsCount)(0).zip(names) do
         if duration > 0 then
            if str.nonEmpty then str.append(", ")
            str.append(duration).append(" ").append(plural(duration, name))
      str.result()
end timeString
