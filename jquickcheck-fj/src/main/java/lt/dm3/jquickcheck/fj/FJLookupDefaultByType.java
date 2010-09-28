package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.impl.TypeResolverRegistry;
import lt.dm3.jquickcheck.internal.Primitives;
import fj.test.Arbitrary;

public class FJLookupDefaultByType implements LookupDefaultByType<Arbitrary<?>> {

    /**
     * Contains all of the default generators for the FJ library. <br />
     * TODO: currently if several generators for the same type exist in Arbitrary, the last one wins (gets in this map).
     */
    private static final Map<Type, Arbitrary<?>> DEFAULTS;

    static {
        Map<Type, Arbitrary<?>> defaults = new HashMap<Type, Arbitrary<?>>();
        Field[] arbFields = Arbitrary.class.getFields();
        for (Field f : arbFields) {
            if (f.getName().startsWith("arb") && f.getGenericType() instanceof ParameterizedType) {
                Type key = TypeResolverRegistry.resolveFrom(f);
                try {
                    Arbitrary<?> value = (Arbitrary<?>) f.get(null);
                    if (Primitives.isPrimitiveOrWrapper(key)) {
                        defaults.put(Primitives.oppositeOf(key), value);
                    }
                    defaults.put(key, value);
                } catch (IllegalArgumentException e) {} catch (IllegalAccessException e) {} // NOPMD - have nowhere to
                                                                                            // report the exception
            }
        }

        DEFAULTS = Collections.unmodifiableMap(defaults);
    }

    private final Lookup<Type, Arbitrary<?>> moreDefaults;

    public FJLookupDefaultByType(Lookup<Type, Arbitrary<?>> defaults) {
        this.moreDefaults = defaults;
    }

    @Override
    public boolean hasDefault(Type t) {
        return DEFAULTS.containsKey(t) || moreDefaults.has(t);
    }

    @Override
    public Arbitrary<?> getDefault(Type t) {
        if (!DEFAULTS.containsKey(t)) {
            if (!moreDefaults.has(t)) {
                throw new IllegalArgumentException("Could not find a default generator for type: " + t);
            }
            return moreDefaults.get(t);
        }
        return DEFAULTS.get(t);
    }

}
