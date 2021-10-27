package tinyscalautils.java;

import java.util.function.Supplier;

/**
 * Java wrappers for timing functions.
 * <p>
 * This class cannot be instantiated.
 * </p>
 *
 * @see tinyscalautils.timing
 * @since 1.0
 */
public class Timing {

  private Timing() {
    throw new AssertionError("this class cannot be instantiated");
  }

  private static final TimingScala timer = new TimingScala();

  public static long getTime() {
    return timer.getTime();
  }

  public static long now() {
    return timer.now();
  }

  public static double timeOf(Runnable code) {
    return timer.timeOf(code);
  }

  public static <A> TimingPair<A> timeIt(Supplier<? extends A> code) {
    return timer.timeIt(code);
  }

  public static <A> A delay(double seconds, long start, Supplier<? extends A> code) {
    return timer.delay(seconds, start, code);
  }

  public static <A> A delay(double seconds, Supplier<? extends A> code) {
    return timer.delay(seconds, code);
  }

  public static void sleep(double seconds, long start) {
    timer.sleep(seconds, start);
  }

  public static void sleep(double seconds) {
    timer.sleep(seconds);
  }
}
