import org.junit.jupiter.api.Test;
import scala.NotImplementedError;

import static org.junit.jupiter.api.Assertions.*;
import static tinyscalautils.java.Assertions.*;

public class AssertionsSuite {
  @Test
  void testRequire() {
    assertDoesNotThrow(() -> require(true));
    var ex = assertThrows(IllegalArgumentException.class, () -> require(false));
    assertNull(ex.getMessage());
  }

  @Test
  void testRequireMessage() {
    assertDoesNotThrow(() -> require(true, "X"));
    var ex = assertThrows(IllegalArgumentException.class, () -> require(false, "%s.%d", "X", 42));
    assertEquals("X.42", ex.getMessage());
  }

  @Test
  void testRequireState() {
    assertDoesNotThrow(() -> requireState(true));
    var ex = assertThrows(IllegalStateException.class, () -> requireState(false));
    assertNull(ex.getMessage());
  }

  @Test
  void testRequireStateMessage() {
    assertDoesNotThrow(() -> requireState(true, "X"));
    var ex = assertThrows(IllegalStateException.class, () -> requireState(false, "%s.%d", "X", 42));
    assertEquals("X.42", ex.getMessage());
  }

  @Test
  void testCheckNonNull() {
    var obj = new Object();
    assertSame(obj, checkNonNull(obj));
    assertThrows(IllegalArgumentException.class, () -> checkNonNull(null));
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test
  void testTODO() {
    assertThrows(NotImplementedError.class, () -> TODO().getClass());
    assertThrows(NotImplementedError.class, () -> TODO("X").getClass());
    var ex = assertThrows(NotImplementedError.class, () -> TODO("X"));
    assertEquals("X", ex.getMessage());
  }
}
