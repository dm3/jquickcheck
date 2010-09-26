package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.impl.resolution.NamedAndTypedGenerator;
import lt.dm3.jquickcheck.api.impl.resolution.CompositeResolution;

public class SampleResolutionFromFields extends CompositeResolution<Generator<?>> {

    @Override
    protected boolean holdsGeneratorInstance(Field field) {
        return Generator.class.isAssignableFrom(field.getType());
    }

    @Override
    protected LookupDefaultByType<Generator<?>> createLookupDefaultByType(Object context) {
        return new SampleLookupDefaultByType();
    }

    @Override
    protected NamedAndTypedGenerator<Generator<?>> createImplicitGenerator(Object context, Method method,
        List<Generator<?>> components) {
        return null;
    }

    @Override
    protected boolean returnsGenerator(Method method) {
        return Generator.class.isAssignableFrom(method.getReturnType());
    }

}
