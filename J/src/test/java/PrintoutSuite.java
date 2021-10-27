import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tinyscalautils.java.Text.STANDARD_MODE.printf;
import static tinyscalautils.java.Text.STANDARD_MODE.println;
import static tinyscalautils.java.Text.printout;

public class PrintoutSuite {
  @Test
  void testPrintout() {
    assertEquals("X\n", printout(() -> println("X")));
    assertEquals("X", printout(() -> printf("%s","X")));
  }

  @Test
  void testPrintoutStandard() {
    assertEquals("X\n", printout(() -> System.out.println("X")));
    assertEquals("X", printout(() -> System.out.printf("%s","X")));
  }
}
