import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tinyscalautils.java.Timing.*;

public class TimingSuite {
  @Test
  void testGetTime() {
    long start = getTime();
    sleep(1.0);
    long end = getTime();
    assertEquals(1.0, (end - start) / 1e9, 0.1);
  }

  @Test
  void testNow() {
    long millis = now();
    assertEquals(System.currentTimeMillis(), millis, 100.0);
  }

  @Test
  void testTimeOf() {
    double time = timeOf(() -> sleep(1.0));
    assertEquals(1.0, time, 0.1);
  }

  @Test
  void testTimeIt() {
    var timedPair = timeIt(() -> delay(1.0, () -> "X"));
    assertEquals(1.0, timedPair.time, 0.1);
    assertEquals("X", timedPair.value);
  }

  @Test
  void testDelay() {
    long start = getTime();
    sleep(1.0);
    var timedPair = timeIt(() -> delay(2.0, start, () -> "X"));
    assertEquals(1.0, timedPair.time, 0.1);
    assertEquals("X", timedPair.value);
  }

  @Test
  void testSleep() {
    long start = getTime();
    assertEquals(1.0, timeOf(() -> sleep(1.0)), 0.1);
    assertEquals(1.0, timeOf(() -> sleep(2.0, start)), 0.1);
  }
}
