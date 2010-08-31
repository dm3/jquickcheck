package lt.dm3.jquickcheck.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface Arb {
    Class<? extends Generator<?>> genClass() default ExceptionalGenerator.class;

    String gen() default "";
}
