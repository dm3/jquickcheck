package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.internal.Primitives;

public class SampleLookupDefaultByType implements LookupDefaultByType<Generator<?>> {

    @Override
    public Generator<?> getDefault(Type t) {
        if (Primitives.isInteger(t)) {
            return new IntegerGenerator();
        } else if (t.equals(Sample.class)) {
            return new SampleGenerator();
        }
        throw new IllegalArgumentException("No default generator for: " + t);
    }

    @Override
    public boolean hasDefault(Type t) {
        try {
            getDefault(t);
            return true;
        } catch (Exception e) {
            System.out.println("No no no no no no no!");
        }
        return false;
    }

}
