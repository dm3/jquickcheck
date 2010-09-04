package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.junit4.Generator;

public class FJGeneratorResolutionStrategy implements GeneratorResolutionStrategy<Generator<?>> {

    @Override
    public <T> GeneratorRepository<Generator<?>> resolve(T context) {
        Field[] fields = context.getClass().getDeclaredFields();
        final List<Field> gens = new ArrayList<Field>(fields.length);
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
        return new FJGeneratorRepository(gens, context);
    }

}
