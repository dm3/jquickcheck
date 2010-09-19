package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Type;
import java.util.Arrays;

import lt.dm3.jquickcheck.api.impl.DefaultGeneratorRepository;
import lt.dm3.jquickcheck.api.impl.NamedAndTypedGenerator;
import lt.dm3.jquickcheck.internal.Primitives;

public class SampleGeneratorRepository extends DefaultGeneratorRepository<Generator<?>> {

    public SampleGeneratorRepository(Iterable<? extends NamedAndTypedGenerator<Generator<?>>> generators) {
        super(generators, null);
    }

    public SampleGeneratorRepository(NamedAndTypedGenerator<Generator<?>>... generators) {
        super(Arrays.asList(generators), null);
    }

    @Override
    public Generator<?> getDefaultGeneratorFor(Type t) {
        if (Primitives.isInteger(t)) {
            return new IntegerGenerator();
        } else if (t.equals(Sample.class)) {
            return new SampleGenerator();
        }
        throw new IllegalArgumentException("No default generator for: " + t);
    }

    @Override
    public boolean hasDefaultGeneratorFor(Type t) {
        try {
            getDefaultGeneratorFor(t);
            return true;
        } catch (Exception e) {
            System.out.println("No no no no no no no!");
        }
        return false;
    }

}
