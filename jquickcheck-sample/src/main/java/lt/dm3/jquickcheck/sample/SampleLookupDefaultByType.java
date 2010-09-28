package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.internal.Primitives;

public class SampleLookupDefaultByType implements LookupDefaultByType<Generator<?>> {

    private final Lookup<Type, Generator<?>> defaults;

    public SampleLookupDefaultByType(Lookup<Type, Generator<?>> defaults) {
        this.defaults = defaults;
    }

    @Override
    public Generator<?> getDefault(Type t) {
        if (Primitives.isInteger(t)) {
            return new IntegerGenerator();
        } else if (t.equals(Sample.class)) {
            return new SampleGenerator();
        } else if (defaults.has(t)) {
            return defaults.get(t);
        }
        throw new IllegalArgumentException("No default generator for: " + t);
    }

    @Override
    public boolean hasDefault(Type t) {
        if (defaults.has(t)) {
            return true;
        }

        try {
            getDefault(t);
            return true;
        } catch (Exception e) {
            System.out.println("No no no no no no no!");
        }
        return false;
    }

}
