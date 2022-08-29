package tinyscalautils.java;

import java.nio.charset.Charset;

/**
 * Java wrappers for {@code text} package.
 *
 * @see tinyscalautils.text
 */
public final class Text {
  private Text() {
    throw new AssertionError("this class cannot be instantiated");
  }

  public final static class SILENT_MODE {
    private SILENT_MODE() {
      throw new AssertionError("this class cannot be instantiated");
    }

    public static void println(Object arg) {
    }

    public static void printf(String format, Object... arg) {
    }
  }

  public final static class STANDARD_MODE {
    private STANDARD_MODE() {
      throw new AssertionError("this class cannot be instantiated");
    }

    public static void println(Object arg) {
      tinyscalautils.text.StandardMode$.MODULE$.print(arg + "\n");
    }

    public static void printf(String format, Object... arg) {
      tinyscalautils.text.StandardMode$.MODULE$.print(String.format(format, arg));
    }
  }

  public final static class THREAD_MODE {
    private THREAD_MODE() {
      throw new AssertionError("this class cannot be instantiated");
    }

    public static void println(Object arg) {
      tinyscalautils.text.ThreadMode$.MODULE$.print(arg + "\n");
    }

    public static void printf(String format, Object... arg) {
      tinyscalautils.text.ThreadMode$.MODULE$.print(String.format(format, arg));
    }
  }

  public final static class THREAD_TIME_DEMO_MODE {
    private THREAD_TIME_DEMO_MODE() {
      throw new AssertionError("this class cannot be instantiated");
    }

    public static void println(Object arg) {
      tinyscalautils.text.ThreadTimeDemoMode$.MODULE$.print(arg + "\n");
    }

    public static void printf(String format, Object... arg) {
      tinyscalautils.text.ThreadTimeDemoMode$.MODULE$.print(String.format(format, arg));
    }
  }

  public final static class THREAD_TIME_MODE {
    private THREAD_TIME_MODE() {
      throw new AssertionError("this class cannot be instantiated");
    }

    public static void println(Object arg) {
      tinyscalautils.text.ThreadTimeMode$.MODULE$.print(arg + "\n");
    }

    public static void printf(String format, Object... arg) {
      tinyscalautils.text.ThreadTimeMode$.MODULE$.print(String.format(format, arg));
    }
  }

  public final static class TIME_DEMO_MODE {
    private TIME_DEMO_MODE() {
      throw new AssertionError("this class cannot be instantiated");
    }

    public static void println(Object arg) {
      tinyscalautils.text.TimeDemoMode$.MODULE$.print(arg + "\n");
    }

    public static void printf(String format, Object... arg) {
      tinyscalautils.text.TimeDemoMode$.MODULE$.print(String.format(format, arg));
    }
  }

  public final static class TIME_MODE {
    private TIME_MODE() {
      throw new AssertionError("this class cannot be instantiated");
    }

    public static void println(Object arg) {
      tinyscalautils.text.TimeMode$.MODULE$.print(arg + "\n");
    }

    public static void printf(String format, Object... arg) {
      tinyscalautils.text.TimeMode$.MODULE$.print(String.format(format, arg));
    }
  }

  private static final TextScala textScala = new TextScala();

  /** Note that, contrary to the Scala variant, this method sets {@code includeSystem} to true by default. */
  public static String printout(Runnable code) {
    return printout(false, true, Charset.defaultCharset(), code);
  }

  public static String printout(boolean includeErr, boolean includeSystem, Runnable code) {
    return printout(includeErr, includeSystem, Charset.defaultCharset(), code);
  }

  public static String printout(boolean includeErr, boolean includeSystem, Charset charset, Runnable code) {
    return textScala.printout(code, includeErr, includeSystem, charset);
  }
  
  public static void info() {
    textScala.info();
  }
}
