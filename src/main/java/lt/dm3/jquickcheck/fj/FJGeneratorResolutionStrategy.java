package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.impl.ResolutionFromFieldsOfType;
import fj.test.Arbitrary;

public class FJGeneratorResolutionStrategy extends ResolutionFromFieldsOfType<Arbitrary<?>> {

    @Override
    protected boolean holdsGeneratorInstance(Field field) {
        return Arbitrary.class.isAssignableFrom(field.getType());
    }

    @Override
    protected GeneratorRepository<Arbitrary<?>> createRepository(Iterable<Field> generators, Object context) {
        return new FJGeneratorRepository(generators, context);
    }

}
