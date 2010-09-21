package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.api.impl.DefaultSynthesizer.Synthesized;

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
                Type type = TypeResolverRegistry.resolveFrom(field);
                if (type == null) {
                    type = TypeResolverRegistry.resolveFrom(generator);
                }
                this.type = type;
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
        // workaround for not being able to pass List<? super NamedAndTypedGenerator<GEN>> into
        // method(List<NamedGenerator<GEN>>)
        final List<NamedGenerator<GEN>> namedGens = new ArrayList<NamedGenerator<GEN>>(fields.length);
        final List<TypedGenerator<GEN>> typedGens = new ArrayList<TypedGenerator<GEN>>(fields.length);
        for (final Field field : fields) {
            if (holdsGeneratorInstance(field)) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        field.setAccessible(true);
                        GeneratorFromField<GEN> genFromField = new GeneratorFromField<GEN>(field, context);
                        namedGens.add(genFromField);
                        typedGens.add(genFromField);
                        return null; // cannot return void as there's no instance of it
                    }
                });
            }
        }
        DefaultGeneratorRepository<GEN> repo = new DefaultGeneratorRepository<GEN>(
                createLookupByName(namedGens), createLookupByType(typedGens),
                createLookupDefaultByType(context), createSynthesizer(context));
        return repo;
    }

    private static class NoDefaultLookup<GEN> implements LookupDefaultByType<GEN> {
        @Override
        public boolean hasDefault(Type t) {
            return false;
        }

        @Override
        public GEN getDefault(Type t) {
            throw new IllegalArgumentException("No default generator for type: " + t);
        }
    }

    protected Lookup<String, GEN> createLookupByName(Iterable<NamedGenerator<GEN>> namedGenerators) {
        return DefaultLookupByName.from(namedGenerators);
    }

    protected Lookup<Type, GEN> createLookupByType(Iterable<TypedGenerator<GEN>> typedGenerators) {
        return DefaultLookupByType.from(typedGenerators);
    }

    protected LookupDefaultByType<GEN> createLookupDefaultByType(Object context) {
        return new NoDefaultLookup<GEN>();
    }

    protected Synthesizer<GEN> createSynthesizer(Object context) {
        return new DefaultSynthesizer<GEN>(Collections.<Synthesized<GEN>> emptyList());
    }

    protected abstract boolean holdsGeneratorInstance(Field field);

}
