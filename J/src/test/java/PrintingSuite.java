import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tinyscalautils.java.Text.*;

public class PrintingSuite {
  @Test
  void testSilent() {
    assertTrue(printout(() -> SILENT_MODE.println("X")).isEmpty());
    assertTrue(printout(() -> SILENT_MODE.printf("%s", "X")).isEmpty());
  }

  @Test
  void testStandard() {
    assertEquals("X\n", printout(() -> STANDARD_MODE.println("X")));
    assertEquals("X", printout(() -> STANDARD_MODE.printf("%s", "X")));
  }

  @Test
  void testThread() {
    assertEquals("main: X\n", printout(() -> THREAD_MODE.println("X")));
    assertEquals("main: X", printout(() -> THREAD_MODE.printf("%s", "X")));
  }

  @Test
  void testTime() {
    var str = printout(() -> TIME_MODE.println("X"));
    assertTrue(str.startsWith("at ") && str.endsWith(": X\n"));
    str = printout(() -> TIME_MODE.printf("%s", "X"));
    assertTrue(str.startsWith("at ") && str.endsWith(": X"));
  }

  @Test
  void testTimeDemo() {
    var str = printout(() -> TIME_DEMO_MODE.println("X"));
    assertTrue(str.startsWith("at XX:XX:") && str.endsWith(": X\n"));
    str = printout(() -> TIME_DEMO_MODE.printf("%s", "X"));
    assertTrue(str.startsWith("at XX:XX:") && str.endsWith(": X"));
  }

  @Test
  void testThreadTime() {
    var str = printout(() -> THREAD_TIME_MODE.println("X"));
    assertTrue(str.startsWith("main at ") && str.endsWith(": X\n"));
    str = printout(() -> THREAD_TIME_MODE.printf("%s", "X"));
    assertTrue(str.startsWith("main at ") && str.endsWith(": X"));
  }

  @Test
  void testThreadTimeDemo() {
    var str = printout(() -> THREAD_TIME_DEMO_MODE.println("X"));
    assertTrue(str.startsWith("main at XX:XX:") && str.endsWith(": X\n"));
    str = printout(() -> THREAD_TIME_DEMO_MODE.printf("%s", "X"));
    assertTrue(str.startsWith("main at XX:XX:") && str.endsWith(": X"));
  }
}
