package lt.dm3.jquickcheck.api.impl.resolution;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.Disabled;
import lt.dm3.jquickcheck.api.impl.TypeResolverRegistry;

public class ResolutionFromFields<GEN> implements GeneratorResolutionStep<GEN> {

    private final Class<?> generatorClass;

    public ResolutionFromFields(Class<?> generatorClass) {
        this.generatorClass = generatorClass;
    }

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
         * @see lt.dm3.jquickcheck.api.impl.resolution.TypedGenerator#getType()
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
    public Iterable<NamedAndTypedGenerator<GEN>> resolveFrom(final Object context) {
        final List<NamedAndTypedGenerator<GEN>> result = new ArrayList<NamedAndTypedGenerator<GEN>>();
        Field[] fields = context.getClass().getDeclaredFields();
        for (final Field field : fields) {
            if (holdsGeneratorInstance(field) && field.getAnnotation(Disabled.class) == null) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        field.setAccessible(true);
                        result.add(new GeneratorFromField<GEN>(field, context));
                        return null; // cannot return void as there's no instance of it
                    }
                });
            }
        }
        return result;
    }

    protected boolean holdsGeneratorInstance(Field field) {
        return generatorClass.isAssignableFrom(field.getType());
    }

}
