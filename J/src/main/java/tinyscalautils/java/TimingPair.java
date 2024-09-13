package tinyscalautils.java;

/** A dedicated pair type that represents a value and the time it took to compute it.
 * Timing pairs are immutable.
 * 
 * @see Timing#timeIt
 * 
 * @since 1.0
 */
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
