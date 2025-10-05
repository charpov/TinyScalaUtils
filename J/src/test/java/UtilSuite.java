import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static tinyscalautils.java.Util.*;

public class UtilSuite {
  @Test
  void testPow() {
    assertThrows(IllegalArgumentException.class, () -> pow(2, -1));
    assertEquals(1, pow(0, 0));
    assertEquals(0, pow(0, 1));
    assertEquals(1, pow(1, 0));
    assertEquals(1, pow(1, 100_000));
    assertEquals(1, pow(2, 0));
    assertEquals(1024, pow(2, 10));
    assertEquals(2048, pow(2, 11));
    assertEquals(1L, pow(2L, 0));
    assertEquals(1073741824L, pow(2L, 30));
    assertEquals(536870912L, pow(2L, 29));
    assertEquals(576460752303423488L, pow(2L, 59));
    assertEquals(59049, pow(3, 10));
    assertEquals(205891132094649L, pow(3L, 30));
  }

  @Test
  void testEvenOddInt() {
    assertTrue(isEven(42));
    assertTrue(isEven(-42));
    assertFalse(isEven(41));
    assertFalse(isEven(-41));
    assertFalse(isOdd(42));
    assertFalse(isOdd(-42));
    assertTrue(isOdd(41));
    assertTrue(isOdd(-41));
  }

  @Test
  void testEvenOddLong() {
    assertTrue(isEven(42L));
    assertTrue(isEven(-42L));
    assertFalse(isEven(41L));
    assertFalse(isEven(-41L));
    assertFalse(isOdd(42L));
    assertFalse(isOdd(-42L));
    assertTrue(isOdd(41L));
    assertTrue(isOdd(-41L));
  }

  @Test
  void testIsPowerOf2Int() {
    assertThrows(IllegalArgumentException.class, () -> isPowerOf2(-1));
    assertFalse(isPowerOf2(3));
    assertFalse(isPowerOf2(5));
    assertFalse(isPowerOf2(6));
    assertFalse(isPowerOf2(7));
    for (int n = 1073741825; n < 2147483647; n++)
         assertFalse(isPowerOf2(n));
    for (int n = 1, c = 0; c < 31; c++, n <<= 1)
         assertTrue(isPowerOf2(n));
  }

  @Test
  void testIsPowerOf2Long() {
    assertThrows(IllegalArgumentException.class, () -> isPowerOf2(-1L));
    assertFalse(isPowerOf2(3L));
    assertFalse(isPowerOf2(5L));
    assertFalse(isPowerOf2(6L));
    assertFalse(isPowerOf2(7L));
    for (long n = 2147483649L; n <= 4294967295L; n++)
         assertFalse(isPowerOf2(n));
    for (long n = 1L, c = 0; c < 63; c++, n <<= 1)
         assertTrue(isPowerOf2(n));
  }
}
