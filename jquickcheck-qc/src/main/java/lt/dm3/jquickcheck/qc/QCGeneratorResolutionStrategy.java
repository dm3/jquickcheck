package lt.dm3.jquickcheck.qc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import lt.dm3.jquickcheck.api.impl.resolution.NamedAndTypedGenerator;
import lt.dm3.jquickcheck.api.impl.resolution.ResolutionFromFieldsOfType;
import net.java.quickcheck.Generator;

public class QCGeneratorResolutionStrategy extends ResolutionFromFieldsOfType<Generator<?>> {

    @Override
    protected boolean holdsGeneratorInstance(Field field) {
        return Generator.class.isAssignableFrom(field.getType());
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
