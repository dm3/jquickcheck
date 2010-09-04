package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;

public abstract class ResolutionFromFieldsOfType<G> implements GeneratorResolutionStrategy<G> {

    @Override
    public final <T> GeneratorRepository<G> resolve(T context) {
        Field[] fields = context.getClass().getDeclaredFields();
        final List<Field> gens = new ArrayList<Field>(fields.length);
        for (final Field field : fields) {
            if (holdsGeneratorInstance(field)) {
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
        return createRepository(gens, context);
    }

    protected abstract boolean holdsGeneratorInstance(Field field);

    protected abstract GeneratorRepository<G> createRepository(Iterable<Field> generators, Object context);
}
