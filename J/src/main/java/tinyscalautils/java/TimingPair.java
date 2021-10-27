package tinyscalautils.java;

import net.jcip.annotations.Immutable;

/** A dedicated pair type that represents a value and the time it took to compute it.
 * 
 * @see Timing#timeIt
 * 
 * @since 1.0
 */
@Immutable
public class TimingPair<A> {
  /** The value part of the pair. */
  public final A value;

  /** The timing part of the pair, in seconds. */
  public final double time;

  TimingPair(A value, double time) {
    this.value = value;
    this.time = time;
  }

  @Override
  public String toString() {
    return "(" + value + "," + time + ")";
  }
}
