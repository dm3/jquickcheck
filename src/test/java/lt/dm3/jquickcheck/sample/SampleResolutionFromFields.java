package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Field;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.impl.HasGenerator;
import lt.dm3.jquickcheck.api.impl.ResolutionFromFieldsOfType;

public class SampleResolutionFromFields extends ResolutionFromFieldsOfType<Generator<?>> {

    @Override
    protected boolean holdsGeneratorInstance(Field field) {
        return Generator.class.isAssignableFrom(field.getType());
    }

    @Override
    protected GeneratorRepository<Generator<?>> createRepository(Iterable<HasGenerator<Generator<?>>> generators,
            Object context) {
        return new NoDefaultGeneratorRepository(generators, context);
    }

}
