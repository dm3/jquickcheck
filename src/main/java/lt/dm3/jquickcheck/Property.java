package lt.dm3.jquickcheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Most of the parameters came from the functionaljava-test library.
 * 
 * @author dm3
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Property {
    /**
     * @return The minimum number of successful tests before a result is reached.
     */
    int minSuccessful() default 100;

    /**
     * @return The maximum number of tests discarded because they did not satisfy pre-conditions.
     */
    int maxDiscarded() default 500;

    /**
     * @return The minimum size to use for checking.
     */
    int minSize() default 0;

    /**
     * @return The maximum size to use for checking.
     */
    int maxSize() default 100;
}
