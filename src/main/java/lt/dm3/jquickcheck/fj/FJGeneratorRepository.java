package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.impl.GeneratorsFromFields;
import lt.dm3.jquickcheck.junit4.Generator;
import fj.test.Arbitrary;

public class FJGeneratorRepository extends GeneratorsFromFields<Generator<?>> {

    public FJGeneratorRepository(Iterable<Field> gens, Object context) {
        super(gens, context);
    }

    @Override
    public Generator<?> getDefaultGeneratorFor(Type t) {
        if (t.equals(Integer.TYPE)) {
            return new FJGenAdapter<Integer>(Arbitrary.arbInteger.gen, 10).toGenerator();
        }
        return null;
    }

    @Override
    protected boolean isGenerator(ParameterizedType pType) {
        return pType.getRawType().equals(Generator.class);
    }

}
