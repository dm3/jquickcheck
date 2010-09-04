package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.junit4.Generator;
import lt.dm3.jquickcheck.junit4.Generators;

public class FJGeneratorResolutionStrategy implements GeneratorResolutionStrategy<Generator<?>> {

    @Override
    public <T> GeneratorRepository<Generator<?>> resolve(T context) {
        Field[] fields = context.getClass().getDeclaredFields();
        final Generators gens = new Generators();
        for (final Field field : fields) {
            if (Generator.class.isAssignableFrom(field.getType())) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        field.setAccessible(true);
                        gens.add(field);
                        return null; // cannot return void as there's no instance of it
                    }
                });
            }
        }
        return new FJGeneratorRepository(gens.forTest(context));
    }

}
