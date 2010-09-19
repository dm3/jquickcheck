package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.api.impl.DefaultGeneratorRepository;
import lt.dm3.jquickcheck.api.impl.NamedAndTypedGenerator;
import lt.dm3.jquickcheck.api.impl.TypeResolverRegistry;
import lt.dm3.jquickcheck.internal.Primitives;
import fj.test.Arbitrary;

public class FJGeneratorRepository extends DefaultGeneratorRepository<Arbitrary<?>> {

    /**
     * Contains all of the default generators for the FJ library. <br />
     * TODO: currently if several generators for the same type exist in Arbitrary, the last one wins (gets in this map).
     */
    private static final Map<Type, Arbitrary<?>> DEFAULTS;

    static {
        Map<Type, Arbitrary<?>> defaults = new HashMap<Type, Arbitrary<?>>();
        Field[] arbFields = Arbitrary.class.getFields();
        for (Field f : arbFields) {
            if (f.getName().startsWith("arb")) {
                if (f.getGenericType() instanceof ParameterizedType) {
                    Type key = TypeResolverRegistry.resolveFrom(f);
                    try {
                        Arbitrary<?> value = (Arbitrary<?>) f.get(null);
                        if (Primitives.isPrimitiveOrWrapper(key)) {
                            defaults.put(Primitives.oppositeOf(key), value);
                        }
                        defaults.put(key, value);
                    } catch (IllegalArgumentException e) {} catch (IllegalAccessException e) {}
                }
            }
        }

        DEFAULTS = Collections.unmodifiableMap(defaults);
    }

    public FJGeneratorRepository(Iterable<NamedAndTypedGenerator<Arbitrary<?>>> generators,
            Synthesizer<Arbitrary<?>> synthesizer) {
        super(generators, synthesizer);
    }

    @Override
    public Arbitrary<?> getDefaultGeneratorFor(Type t) {
        return DEFAULTS.get(t);
    }

}
