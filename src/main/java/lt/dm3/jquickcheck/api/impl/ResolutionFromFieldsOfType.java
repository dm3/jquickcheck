package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;

public abstract class ResolutionFromFieldsOfType<GEN> implements GeneratorResolutionStrategy<GEN> {

    private static final class GeneratorFromField<GEN> implements NamedAndTypedGenerator<GEN> {
        private final String name;
        private final Type type;
        private final GEN generator;

        @SuppressWarnings("unchecked")
        GeneratorFromField(Field field, Object context) {
            try {
                this.generator = (GEN) field.get(context);
                this.name = field.getName();

                final Type type = field.getGenericType();
                Type result = null;
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    if (pType.getActualTypeArguments().length == 1) {
                        result = pType.getActualTypeArguments()[0];
                    }
                }
                this.type = result;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Cannot resolve the value of the generator!", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Cannot resolve the value of the generator!", e);
            }
        }

        @Override
        public GEN getGenerator() {
            return generator;
        }

        /**
         * Might be null
         * 
         * @see lt.dm3.jquickcheck.api.impl.TypedGenerator#getType()
         */
        @Override
        public Type getType() {
            return type;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    public final <T> GeneratorRepository<GEN> resolve(final T context) {
        Field[] fields = context.getClass().getDeclaredFields();
        final List<NamedAndTypedGenerator<GEN>> gens = new ArrayList<NamedAndTypedGenerator<GEN>>(fields.length);
        for (final Field field : fields) {
            if (holdsGeneratorInstance(field)) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        field.setAccessible(true);
                        gens.add(new GeneratorFromField<GEN>(field, context));
                        return null; // cannot return void as there's no instance of it
                    }
                });
            }
        }
        return createRepository(gens, context);
    }

    protected abstract boolean holdsGeneratorInstance(Field field);

    protected abstract GeneratorRepository<GEN> createRepository(Iterable<NamedAndTypedGenerator<GEN>> generators, Object context);
}
