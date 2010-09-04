package lt.dm3.jquickcheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lt.dm3.jquickcheck.internal.ExceptionalGenerator;
import lt.dm3.jquickcheck.junit4.Generator;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface G {
    Class<? extends Generator<?>> genClass() default ExceptionalGenerator.class;

    String gen() default "";
}
