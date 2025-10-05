package tinyscalautils.java;

public class Util {
  private Util() {
    throw new AssertionError("this class cannot be instantiated");
  }

  private static final UtilScala util = new UtilScala();

  public static boolean isEven(int n) {
    return (n & 1) == 0;
  }

  public static boolean isEven(long n) {
    return (n & 1L) == 0L;
  }

  public static boolean isOdd(int n) {
    return (n & 1) != 0;
  }

  public static boolean isOdd(long n) {
    return (n & 1L) != 0L;
  }

  public static int pow(int n, int m) {
    return util.pow(n, m);
  }

  public static long pow(long n, int m) {
    return util.pow(n, m);
  }
  
  public static boolean isPowerOf2(int n) {
    return util.isPowerOf2(n);
  }

  public static boolean isPowerOf2(long n) {
    return util.isPowerOf2(n);
  }
}
