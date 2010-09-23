package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lt.dm3.jquickcheck.Disabled;
import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.Property;
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
            if (holdsGeneratorInstance(field) && field.getAnnotation(Disabled.class) == null) {
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
        Method[] methods = context.getClass().getDeclaredMethods();
        for (final Method method : methods) {
            if (returnsGenerator(method) && method.getAnnotation(G.class) != null) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        method.setAccessible(true);
                        NamedAndTypedGenerator<GEN> genFromMethod = new GeneratorFromMethod<GEN>(method, context);
                        namedGens.add(genFromMethod);
                        typedGens.add(genFromMethod);
                        return null;
                    }
                });
            }
        }
        LookupDefaultByType<GEN> lookupDefault = createLookupDefaultByType(context);
        Synthesizer<GEN> synthesizer = createSynthesizer(context);
        DefaultGeneratorRepository<GEN> repo = new DefaultGeneratorRepository<GEN>(
                createLookupByName(namedGens), createLookupByType(typedGens),
                lookupDefault, synthesizer);
        List<NamedAndTypedGenerator<GEN>> implicit = createImplicitGenerators(context, repo);
        namedGens.addAll(implicit);
        typedGens.addAll(implicit);
        repo = new DefaultGeneratorRepository<GEN>(
                createLookupByName(namedGens), createLookupByType(typedGens),
                lookupDefault, synthesizer);
        return repo;
    }

    protected List<NamedAndTypedGenerator<GEN>> createImplicitGenerators(Object context, GeneratorRepository<GEN> repo) {
        Method[] methods = context.getClass().getDeclaredMethods();
        List<NamedAndTypedGenerator<GEN>> result = new ArrayList<NamedAndTypedGenerator<GEN>>();
        // TODO: reuse DefaultPropertyMethod
        for (final Method method : methods) {
            if (method.getReturnType() != null && !returnsGenerator(method)
                    && method.getAnnotation(G.class) != null &&
                    method.getAnnotation(Property.class) == null) {
                Type[] params = method.getGenericParameterTypes();
                List<GEN> components = new ArrayList<GEN>(params.length);
                for (Type t : params) {
                    if (repo.has(t)) {
                        components.add(repo.get(t));
                    } else if (repo.hasDefault(t)) {
                        components.add(repo.getDefault(t));
                    } else {
                        components.add(new DefaultRequestToSynthesize<GEN>(t, new DefaultInvocationSettings())
                                .synthesize(repo));
                    }
                }
                result.add(createImplicitGenerator(context, method, components));
            }
        }
        return result;
    }

    /**
     * Cannot make it return just a GEN as I won't be able to determine its generic type parameter.
     * 
     * @param context
     * @param method
     * @param components
     * @return
     */
    protected abstract NamedAndTypedGenerator<GEN> createImplicitGenerator(Object context, Method method,
        List<GEN> components);

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

    protected abstract boolean returnsGenerator(Method method);

}
