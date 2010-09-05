package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Type;
import java.util.Arrays;

import lt.dm3.jquickcheck.api.impl.DefaultGeneratorRepository;
import lt.dm3.jquickcheck.api.impl.NamedAndTypedGenerator;
import lt.dm3.jquickcheck.internal.Primitives;

public class SampleGeneratorRepository extends DefaultGeneratorRepository<Generator<?>> {

    public SampleGeneratorRepository(Iterable<? extends NamedAndTypedGenerator<Generator<?>>> generators) {
        super(generators);
    }

    public SampleGeneratorRepository(NamedAndTypedGenerator<Generator<?>>... generators) {
        super(Arrays.asList(generators));
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

}
