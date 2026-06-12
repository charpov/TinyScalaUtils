package tinyscalautils.test.tags;

import org.scalatest.TagAnnotation;

import java.lang.annotation.*;

/**
 * {@code NoTimeout} annotation.
 *
 * @see tinyscalautils.test.tagobjects.NoTimeout
 */
@TagAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface NoTimeout {}
