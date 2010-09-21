package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.Sample;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

public class GeneratorResolutionStrategyTest {

    static class TestResolver extends ResolutionFromFieldsOfType<Generator<?>> {

        @Override
        protected boolean holdsGeneratorInstance(Field field) {
            return Generator.class.isAssignableFrom(field.getType());
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
