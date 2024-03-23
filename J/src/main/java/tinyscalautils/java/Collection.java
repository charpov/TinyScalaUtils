package tinyscalautils.java;

import java.util.*;

public class Collection {
  private Collection() {
    throw new AssertionError("this class cannot be instantiated");
  }

  private static final CollectionScala collection = new CollectionScala();

  public static <A> Iterator<A> circular(Iterable<A> iterable) {
    return collection.circular(iterable);
  }

  public static <A> Iterator<A> randomly(Iterable<A> iterable, Random rand) {
    return collection.randomly(iterable, rand);
  }

  public static <A> A pickOne(Iterable<A> iterable, Random rand) {
    return collection.pickOne(iterable, rand);
  }

  public static <A> Optional<A> pickOneOption(Iterable<A> iterable, Random rand) {
    return collection.pickOneOption(iterable, rand);
  }

  public static class JavaList {
    private JavaList() {
      throw new AssertionError("this class cannot be instantiated");
    }

    @SafeVarargs
    public static <A> List<A> of(A... values) {
      return new ArrayList<>(Arrays.asList(values));
    }
  }
}
