package lt.dm3.jquickcheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lt.dm3.jquickcheck.api.impl.DefaultInvocationSettings;

/**
 * Most of the parameters came from the functionaljava-test library.
 * 
 * @author dm3
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Property {

    boolean useDefaults() default DefaultInvocationSettings.DEFAULT_USE_DEFAULTS;

    /**
     * Defaults to true
     * 
     * @return true if synthetic generators should be constructed when no explicit generator can be found for some type
     */
    boolean useSynthetics() default DefaultInvocationSettings.DEFAULT_USE_SYNTHETICS;

    /**
     * @return The minimum number of successful tests before a result is reached.
     */
    int minSuccessful() default DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL;

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
