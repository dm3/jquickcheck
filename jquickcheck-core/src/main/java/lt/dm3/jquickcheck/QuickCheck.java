package lt.dm3.jquickcheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lt.dm3.jquickcheck.api.impl.DefaultInvocationSettings;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QuickCheck {

    public Class<? extends Provider<?>> provider();

    boolean useDefaults() default DefaultInvocationSettings.DEFAULT_USE_DEFAULTS;

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
