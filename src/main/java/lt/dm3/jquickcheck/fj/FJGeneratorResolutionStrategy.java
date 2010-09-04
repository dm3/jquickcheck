package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.junit4.Generator;
import lt.dm3.jquickcheck.junit4.Generators;

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
