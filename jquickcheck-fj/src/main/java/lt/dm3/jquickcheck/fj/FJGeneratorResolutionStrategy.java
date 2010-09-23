package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.api.impl.NamedAndTypedGenerator;
import lt.dm3.jquickcheck.api.impl.ResolutionFromFieldsOfType;

import com.googlecode.gentyref.GenericTypeReflector;

import fj.F;
import fj.test.Arbitrary;

public class FJGeneratorResolutionStrategy extends ResolutionFromFieldsOfType<Arbitrary<?>> {

    @Override
    protected boolean holdsGeneratorInstance(Field field) {
        return Arbitrary.class.isAssignableFrom(field.getType());
    }

    @Override
    protected LookupDefaultByType<Arbitrary<?>> createLookupDefaultByType(Object context) {
        return new FJLookupDefaultByType();
    }

    @Override
    protected Synthesizer<Arbitrary<?>> createSynthesizer(Object context) {
        return new FJSynthesizer();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected NamedAndTypedGenerator<Arbitrary<?>> createImplicitGenerator(final Object context, final Method method,
        final List<Arbitrary<?>> components) {
        Type generatorType = GenericTypeReflector.getExactReturnType(method, context.getClass());
        if (components.size() == 1) {
            return new GeneratorOfTypeWithName(generatorType, method.getName(),
                    Arbitrary.arbitrary(components.get(0).gen.map(new F() {
                        @Override
                        public Object f(Object a) {
                            try {
                                return method.invoke(context, a);
                            } catch (Exception e) {
                                throw new QuickCheckException("Could not generate a value for generator: " + method);
                            }
                        }
                    })));
        }
        throw new IllegalArgumentException("Unsupported number of generator components: " + components);
    }

    private static class GeneratorOfTypeWithName implements NamedAndTypedGenerator<Arbitrary<?>> {
        private final String name;
        private final Type type;
        private final Arbitrary<?> generator;

        public GeneratorOfTypeWithName(Type type, String name, Arbitrary<?> generator) {
            this.name = name;
            this.type = type;
            this.generator = generator;
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        public Arbitrary<?> getGenerator() {
            return generator;
        }
    }

    @Override
    protected boolean returnsGenerator(Method method) {
        return Arbitrary.class.isAssignableFrom(method.getReturnType());
    }

}
