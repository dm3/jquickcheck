package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.impl.GeneratorsFromFields;

public class NoDefaultGeneratorRepository extends GeneratorsFromFields<Generator<?>> {

    public NoDefaultGeneratorRepository(Iterable<Field> fields, Object context) {
        super(fields, context);
    }

    @Override
    public Generator<?> getDefaultGeneratorFor(Type t) {
        throw new UnsupportedOperationException("No default generators!");
    }

}
