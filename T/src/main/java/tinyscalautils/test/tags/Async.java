package tinyscalautils.test.tags;

import org.scalatest.TagAnnotation;

import java.lang.annotation.*;

/**
 * {@code Async} annotation.
 *
 * @see tinyscalautils.test.tagobjects.Async
 */
@TagAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface Async {}
