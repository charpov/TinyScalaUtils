import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tinyscalautils.java.Collection.*;

public class CollectionSuite {
  @Test
  void testCircular() {
    Iterator<String> iter = circular(List.of("A", "B", "C"));
    assertEquals("A", iter.next());
    assertEquals("B", iter.next());
    assertEquals("C", iter.next());
    assertEquals("A", iter.next());
    assertEquals("B", iter.next());
    assertEquals("C", iter.next());
    assertEquals("A", iter.next());
    assertTrue(iter.hasNext());
  }

  @Test
  void testRandomly() {
    var strings = Set.of("A", "B", "C");
    Iterator<String> iter = randomly(strings, new Random());
    int count = 0;
    do {
      String str = iter.next();
      assertTrue(strings.contains(str));
      if (str.equals("A")) count += 1;
      else if (str.equals("B")) count -= 1;
    } while (count < 100);
  }

  @Test
  void testPickOne() {
    var strings = Set.of("A", "B", "C");
    var rand = new Random();
    int count = 0;
    do {
      String str = pickOne(strings, rand);
      assertTrue(strings.contains(str));
      if (str.equals("A")) count += 1;
      else if (str.equals("B")) count -= 1;
    } while (count < 100);
  }

  @Test
  void testPickOneOption() {
    var strings = Set.of("A", "B", "C");
    var rand = new Random();
    int count = 0;
    do {
      Optional<String> opt = pickOneOption(strings, rand);
      assertTrue(opt.isPresent());
      String str = opt.get();
      assertTrue(strings.contains(str));
      if (str.equals("A")) count += 1;
      else if (str.equals("B")) count -= 1;
    } while (count < 100);
    assertTrue(pickOneOption(Set.of(), rand).isEmpty());
  }
}
