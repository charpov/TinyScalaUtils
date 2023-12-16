import org.scalatest.events.Event
import org.scalatest.{ Args, Reporter }
import tinyscalautils.lang.unit

val silent: Args = Args(_ => unit)

object R extends Reporter:
   var lastEvent: Option[Event] = None
   def apply(ev: Event): Unit = lastEvent = Some(ev)
