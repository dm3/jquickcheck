package lt.dm3.jquickcheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QuickCheck {

    public Class<? extends GeneratorResolutionStrategy<?>> resolutionStrategy();

}
