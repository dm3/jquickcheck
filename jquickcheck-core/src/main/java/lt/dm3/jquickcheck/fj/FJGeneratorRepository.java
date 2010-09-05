package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.impl.DefaultGeneratorRepository;
import lt.dm3.jquickcheck.api.impl.NamedAndTypedGenerator;
import fj.test.Arbitrary;

public class FJGeneratorRepository extends DefaultGeneratorRepository<Arbitrary<?>> {

    public FJGeneratorRepository(Iterable<NamedAndTypedGenerator<Arbitrary<?>>> generators) {
        super(generators);
    }

    @Override
    public Arbitrary<?> getDefaultGeneratorFor(Type t) {
        if (t.equals(Integer.TYPE)) {
            return Arbitrary.arbInteger;
        }
        return null;
    }

}
