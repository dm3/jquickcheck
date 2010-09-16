package lt.dm3.jquickcheck.qc;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.impl.DefaultGeneratorRepository;
import lt.dm3.jquickcheck.api.impl.NamedAndTypedGenerator;
import lt.dm3.jquickcheck.api.impl.ResolutionFromFieldsOfType;
import net.java.quickcheck.Generator;

public class QCGeneratorResolutionStrategy extends ResolutionFromFieldsOfType<Generator<?>> {

    @Override
    protected boolean holdsGeneratorInstance(Field field) {
        return Generator.class.isAssignableFrom(field.getType());
    }

    @Override
    protected GeneratorRepository<Generator<?>> createRepository(
        Iterable<NamedAndTypedGenerator<Generator<?>>> generators, Object context) {
        return new DefaultGeneratorRepository<Generator<?>>(generators) {
            @Override
            public Generator<?> getDefaultGeneratorFor(Type t) {
                return null;
            }
        };
    }
}
