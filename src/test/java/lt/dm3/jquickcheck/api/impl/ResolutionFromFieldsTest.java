package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.Sample;
import lt.dm3.jquickcheck.sample.SampleGenerator;
import lt.dm3.jquickcheck.sample.SampleResolutionFromFields;

import org.junit.Test;

public class ResolutionFromFieldsTest {

    private final GeneratorResolutionStrategy<Generator<?>> strategy = new SampleResolutionFromFields();

    private abstract static class FieldTest {
        // getter only needed for test
        abstract Generator<Sample> gen();
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

    @Test
    public void shouldResolveAllOfTheSpecifiedGenerators() {
        checkAccessible(new WithOneDefaultField());
        checkAccessible(new WithOnePublicField());
        checkAccessible(new WithOnePrivateField());
        checkAccessible(new WithOnePrivateStaticField());
    }

    @SuppressWarnings("rawtypes")
    private void checkAccessible(FieldTest instance) {
        GeneratorRepository<Generator<?>> repo = strategy.resolve(instance);

        Generator gen = instance.gen();
        assertThat(repo.hasGeneratorFor("gen"), is(true));
        assertThat(repo.getGeneratorFor("gen"), is(gen));
        assertThat(repo.hasGeneratorFor(Sample.class), is(true));
        assertThat(repo.getGeneratorFor(Sample.class), is(gen));
    }
}
