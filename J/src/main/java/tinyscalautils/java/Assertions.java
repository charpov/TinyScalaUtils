package tinyscalautils.java;

import scala.NotImplementedError;

/**
 * Java wrappers for assertion functions.
 * Also defines a {@code TODO} method to mimic Scala's {@code ???}.
 * <p>
 * This class cannot be instantiated.
 * </p>
 *
 * @see tinyscalautils.assertions
 * @since 1.0
 */
public class Assertions {

  private Assertions() {
    throw new AssertionError("this class cannot be instantiated");
  }

  private static final AssertionsScala assertions = new AssertionsScala();

  public static <A> A TODO() {
    throw new NotImplementedError();
  }

  public static <A> A TODO(String message) {
    throw new NotImplementedError(message);
  }

  public static void require(boolean condition) {
    assertions.require(condition);
  }

  public static void require(boolean condition, String message, Object... args) {
    assertions.require(condition, message, args);
  }

  public static void requireState(boolean condition) {
    assertions.requireState(condition);
  }

  public static void requireState(boolean condition, String message, Object... args) {
    assertions.requireState(condition, message, args);
  }

  public static <A> A checkNonNull(A obj) {
    return assertions.checkNonNull(obj);
  }
}
