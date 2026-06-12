package tinyscalautils.test.tags;

import org.scalatest.TagAnnotation;

import java.lang.annotation.*;

/**
 * {@code Points} annotation.
 *
 * @see tinyscalautils.test.tagobjects.Fail
 */
@TagAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface Points {}
