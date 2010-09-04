package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.GeneratorHolder;
import lt.dm3.jquickcheck.sample.NoDefaultGeneratorRepository;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

public class DefaultGeneratorRepositoryTest {

    private GeneratorRepository<Generator<?>> repo;

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoGeneratorOfTheSpecifiedTypeExists() {
        repo = new NoDefaultGeneratorRepository(Collections.<GeneratorHolder> emptyList());

        repo.getGeneratorFor(this.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoGeneratorOfTheSpecifiedNameExists() {
        repo = new NoDefaultGeneratorRepository(Collections.<GeneratorHolder> emptyList());

        repo.getGeneratorFor("name");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldGetGeneratorForType() {
        Class<Integer> type = int.class;
        SampleGenerator generator = new SampleGenerator();
        GeneratorHolder holder = new GeneratorHolder(type, "a", generator);
        repo = new NoDefaultGeneratorRepository(Arrays.asList(holder));

        assertThat(repo.hasGeneratorFor(type), is(true));
        assertThat(repo.getGeneratorFor(type), is((Generator) generator));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldGetGeneratorForName() {
        String name = "a";
        SampleGenerator generator = new SampleGenerator();
        GeneratorHolder holder = new GeneratorHolder(int.class, name, generator);
        repo = new NoDefaultGeneratorRepository(Arrays.asList(holder));

        assertThat(repo.hasGeneratorFor(name), is(true));
        assertThat(repo.getGeneratorFor(name), is((Generator) generator));
    }
}
