package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;

import lt.dm3.jquickcheck.GeneratorRepository;
import lt.dm3.jquickcheck.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.junit.runners.Generator;
import lt.dm3.jquickcheck.junit.runners.Generators;

public class FJGeneratorResolutionStrategy implements GeneratorResolutionStrategy<Generator<?>> {

    @Override
    public <T> GeneratorRepository<Generator<?>> resolve(T context) {
        Field[] fields = context.getClass().getDeclaredFields();
        Generators gens = new Generators();
        for (Field field : fields) {
            if (Generator.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                gens.add(field);
            }
        }
        return new FJGeneratorRepository(gens.forTest(context));
    }

}
