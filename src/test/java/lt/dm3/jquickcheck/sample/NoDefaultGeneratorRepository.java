package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.impl.DefaultGeneratorRepository;
import lt.dm3.jquickcheck.api.impl.NamedAndTypedGenerator;

public class NoDefaultGeneratorRepository extends DefaultGeneratorRepository<Generator<?>> {

    public NoDefaultGeneratorRepository(Iterable<? extends NamedAndTypedGenerator<Generator<?>>> generators) {
        super(generators);
    }

    @Override
    public Generator<?> getDefaultGeneratorFor(Type t) {
        throw new UnsupportedOperationException("No default generators!");
    }

}
