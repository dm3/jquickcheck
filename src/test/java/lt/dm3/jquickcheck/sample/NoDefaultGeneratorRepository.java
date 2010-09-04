package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.impl.GeneratorsFromFields;
import lt.dm3.jquickcheck.api.impl.HasGenerator;

public class NoDefaultGeneratorRepository extends GeneratorsFromFields<Generator<?>> {

    public NoDefaultGeneratorRepository(Iterable<HasGenerator<Generator<?>>> generators, Object context) {
        super(generators, context);
    }

    @Override
    public Generator<?> getDefaultGeneratorFor(Type t) {
        throw new UnsupportedOperationException("No default generators!");
    }

}
