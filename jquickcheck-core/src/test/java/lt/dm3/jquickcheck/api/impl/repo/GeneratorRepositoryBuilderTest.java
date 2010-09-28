package lt.dm3.jquickcheck.api.impl.repo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.impl.resolution.NamedAndTypedGenerator;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Before;
import org.junit.Test;

public class GeneratorRepositoryBuilderTest {

    private static class DefaultNamedAndTypedGen implements NamedAndTypedGenerator<Generator<?>> {

        @Override
        public String getName() {
            return "x";
        }

        @Override
        public Generator<?> getGenerator() {
            return new SampleGenerator();
        }

        @Override
        public Type getType() {
            return this.getClass();
        }

        @Override
        public boolean isDefault() {
            return true;
        }

    }

    private GeneratorRepositoryBuilder<Generator<?>> builder;

    @Before
    public void before() {
        builder = new GeneratorRepositoryBuilder<Generator<?>>(null);
    }

    @Test
    public void shouldCreateADefaultLookupWithDefaultGenerators() {
        builder.add(new DefaultNamedAndTypedGen());

        GeneratorRepository<Generator<?>> repo = builder.build();

        assertThat(repo.has(DefaultNamedAndTypedGen.class), is(false));
        assertThat(repo.hasDefault(DefaultNamedAndTypedGen.class), is(true));
    }
}
