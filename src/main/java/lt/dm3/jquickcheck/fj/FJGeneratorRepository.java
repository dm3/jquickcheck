package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.GeneratorRepository;
import lt.dm3.jquickcheck.junit4.Generator;
import fj.test.Arbitrary;

public class FJGeneratorRepository implements GeneratorRepository<Generator<?>> {

    private final GeneratorRepository<Generator<?>> generatorRepository;

    public FJGeneratorRepository(GeneratorRepository<Generator<?>> generatorRepository) {
        this.generatorRepository = generatorRepository;
    }

    @Override
    public Generator<?> getDefaultGeneratorFor(Type t) {
        if (t.equals(Integer.TYPE)) {
            return new FJGenAdapter<Integer>(Arbitrary.arbInteger.gen, 10).toGenerator();
        }
        return null;
    }

    @Override
    public boolean hasGeneratorFor(String fieldName) {
        return generatorRepository.hasGeneratorFor(fieldName);
    }

    @Override
    public Generator<?> getGeneratorFor(String fieldName) {
        return generatorRepository.getGeneratorFor(fieldName);
    }

    @Override
    public boolean hasGeneratorFor(Type t) {
        return generatorRepository.hasGeneratorFor(t);
    }

    @Override
    public Generator<?> getGeneratorFor(Type t) {
        return generatorRepository.getGeneratorFor(t);
    }

}
