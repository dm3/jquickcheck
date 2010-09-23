package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import lt.dm3.jquickcheck.Disabled;
import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;
import lt.dm3.jquickcheck.sample.Sample;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

public class GeneratorResolutionStrategyTest {

    static class TestResolver extends ResolutionFromFieldsOfType<Generator<?>> {

        @Override
        protected boolean holdsGeneratorInstance(Field field) {
            return Generator.class.isAssignableFrom(field.getType());
        }

        @Override
        protected boolean returnsGenerator(Method method) {
            return Generator.class.isAssignableFrom(method.getReturnType());
        }

        @Override
        protected NamedAndTypedGenerator<Generator<?>> createImplicitGenerator(final Object context,
            final Method method,
            final List<Generator<?>> components) {
            return new NamedAndTypedGenerator<Generator<?>>() {
                @Override
                public Type getType() {
                    return method.getGenericReturnType();
                }

                @Override
                public String getName() {
                    return method.getName();
                }

                @Override
                public Generator<?> getGenerator() {
                    return new Generator<Object>() {
                        @Override
                        public Object generate() {
                            try {
                                return method.invoke(context, components.get(0));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
            };
        }
    }

    private final GeneratorResolutionStrategy<Generator<?>> strategy = new TestResolver();

    public abstract static class FieldTest {
        // getter only needed for test
        Generator<Sample> gen() {
            throw new UnsupportedOperationException("No field found!");
        }

    }

    public static class WithOneDefaultField extends FieldTest {
        Generator<Sample> gen = new SampleGenerator();

        @Override
        Generator<Sample> gen() {
            return gen;
        }
    }

    public static class WithOnePublicField extends FieldTest {
        public Generator<Sample> gen = new SampleGenerator();

        @Override
        Generator<Sample> gen() {
            return gen;
        }
    }

    public static class WithOnePrivateField extends FieldTest {
        private final Generator<Sample> gen = new SampleGenerator();

        @Override
        Generator<Sample> gen() {
            return gen;
        }
    }

    public static class WithOnePrivateStaticField extends FieldTest {
        private static final Generator<Sample> gen = new SampleGenerator();

        @Override
        Generator<Sample> gen() {
            return gen;
        }
    }

    public static class WithOneFieldHavingNoTypeParameter extends FieldTest {
        private static final Generator gen = new SampleGenerator();

        @Override
        Generator<Sample> gen() {
            return gen;
        }
    }

    public static class WithOneFieldHavingNoTypeParameterAndAnonymousGenerator extends FieldTest {
        private static final Generator gen = new Generator<Sample>() {
            @Override
            public Sample generate() {
                return new Sample();
            }
        };

        @Override
        Generator<Sample> gen() {
            return gen;
        }
    }

    public static class WithOneFieldHavingNoTypeParameterAndAnonymousGeneratorWithNoTypeParameter extends FieldTest {
        private static final Generator gen = new Generator() {
            @Override
            public Object generate() {
                return new Sample();
            }
        };

        @Override
        Generator<Sample> gen() {
            return gen;
        }
    }

    public static class WithNonGeneratorFields extends FieldTest {
        private final int field = 1;
        private final Generator<Sample> gen = new SampleGenerator();

        @Override
        Generator<Sample> gen() {
            return gen;
        }
    }

    public static class WithGeneratorCreatedByMethod {
        @G
        public Generator<Integer> makeGen() {
            return new IntegerGenerator();
        }
    }

    @Test
    public void shouldResolveGeneratorFromMethodWithNoArguments() {
        WithGeneratorCreatedByMethod instance = new WithGeneratorCreatedByMethod();

        GeneratorRepository<Generator<?>> repo = strategy.resolve(instance);

        assertThat(repo.has("makeGen"), is(true));
        assertThat(repo.has(Integer.class), is(true));
    }

    public static class WithDisabledGeneratorInField {
        @Disabled
        private final Generator<Sample> gen = new SampleGenerator();

    }

    @Test
    public void shouldNotResolveADisabledGenerator() {
        WithDisabledGeneratorInField instance = new WithDisabledGeneratorInField();

        GeneratorRepository<Generator<?>> repo = strategy.resolve(instance);

        assertThat(repo.has("gen"), is(false));
        assertThat(repo.has(Integer.class), is(false));
    }

    public static class WithImplicitGeneratorCreatedByMethod {
        private final Generator<Integer> intGen = new IntegerGenerator();

        @G
        public String makeGen(Integer x) {
            return x.toString();
        }
    }

    @Test
    public void shouldResolveAnImplicitGeneratorIfComponentGeneratorExists() {
        WithImplicitGeneratorCreatedByMethod instance = new WithImplicitGeneratorCreatedByMethod();

        GeneratorRepository<Generator<?>> repo = strategy.resolve(instance);

        assertThat(repo.has("makeGen"), is(true));
        assertThat(repo.has(Integer.class), is(true));
        assertThat(repo.has(String.class), is(true));
    }

    @Test
    public void shouldNotFailOnClassesHavingNonGeneratorFields() {
        checkAccessible(new WithNonGeneratorFields());
    }

    @Test
    public void shouldResolveAllOfTheSpecifiedGenerators() {
        checkAccessible(new WithOneDefaultField());
        checkAccessible(new WithOnePublicField());
        checkAccessible(new WithOnePrivateField());
        checkAccessible(new WithOnePrivateStaticField());
    }

    @Test
    public void shouldResolveGeneratorsWithDifferentTypingOptions() {
        checkAccessible(new WithOneFieldHavingNoTypeParameter());
        checkAccessible(new WithOneFieldHavingNoTypeParameterAndAnonymousGenerator());
    }

    @Test
    public void shouldNotResolveGeneratorWithNoTypes() {
        checkNotAccessibleByType(new WithOneFieldHavingNoTypeParameterAndAnonymousGeneratorWithNoTypeParameter());
        checkAccessibleByName(new WithOneFieldHavingNoTypeParameterAndAnonymousGeneratorWithNoTypeParameter());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGettingUnaccessibleGenerator() {
        GeneratorRepository<Generator<?>> repo = strategy
                .resolve(new WithOneFieldHavingNoTypeParameterAndAnonymousGeneratorWithNoTypeParameter());

        repo.get(Sample.class);
    }

    private void checkNotAccessibleByType(FieldTest instance) {
        GeneratorRepository<Generator<?>> repo = strategy.resolve(instance);

        assertThat(repo.has(Sample.class), is(false));
    }

    @SuppressWarnings("rawtypes")
    private void checkAccessible(FieldTest instance) {
        GeneratorRepository<Generator<?>> repo = strategy.resolve(instance);

        Generator gen = instance.gen();
        checkAccessibleByName(repo, gen);
        checkAccessibleByType(repo, gen);
    }

    private void checkAccessibleByName(FieldTest instance) {
        checkAccessibleByName(strategy.resolve(instance), instance.gen());
    }

    @SuppressWarnings("rawtypes")
    private void checkAccessibleByName(GeneratorRepository<Generator<?>> repo, Generator gen) {
        assertThat(repo.has("gen"), is(true));
        assertThat(repo.get("gen"), is(gen));
    }

    @SuppressWarnings("rawtypes")
    private void checkAccessibleByType(GeneratorRepository<Generator<?>> repo, Generator gen) {
        assertThat(repo.has(Sample.class), is(true));
        assertThat(repo.get(Sample.class), is(gen));
    }
}
