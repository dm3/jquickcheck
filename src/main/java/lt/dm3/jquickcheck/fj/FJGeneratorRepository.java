package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.impl.GeneratorsFromFields;
import fj.test.Arbitrary;

public class FJGeneratorRepository extends GeneratorsFromFields<Arbitrary<?>> {

    public FJGeneratorRepository(Iterable<Field> gens, Object context) {
        super(gens, context);
    }

    @Override
    public Arbitrary<?> getDefaultGeneratorFor(Type t) {
        if (t.equals(Integer.TYPE)) {
            return Arbitrary.arbInteger;
        }
        return null;
    }

    /**
     * Java makes it impossible to determine the runtime type parameter of a parameterized type. {@code Arbitrary} is
     * final, thus subclassing with specifying exact parameter type is impossible. If we were to
     * 
     * <pre>
     * return object.getClass().getTypeParameters()[0];
     * </pre>
     * 
     * Type returned by this method would never match the type requested from {@code GeneratorRepository}.
     * 
     * @see lt.dm3.jquickcheck.api.impl.GeneratorsFromFields#getGeneratorTypeFor(java.lang.Object)
     */
    @Override
    protected Type getGeneratorTypeFor(Arbitrary<?> object) {
        throw new UnsupportedOperationException("Impossible to determine parameter type for arbitrary: " + object);
    }

}
