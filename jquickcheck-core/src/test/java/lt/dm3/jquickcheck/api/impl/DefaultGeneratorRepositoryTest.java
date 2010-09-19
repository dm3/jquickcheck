package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.RequestToSynthesize;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.GeneratorHolder;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

public class DefaultGeneratorRepositoryTest {

    private GeneratorRepository<Generator<?>> repo;

    static class TestRepo extends DefaultGeneratorRepository<Generator<?>> {

        public TestRepo(Iterable<? extends NamedAndTypedGenerator<Generator<?>>> generators) {
            super(generators, null);
        }

        @Override
        public Generator<?> getDefaultGeneratorFor(Type t) {
            throw new UnsupportedOperationException("I heard you liked exceptions.");
        }

        @Override
        public boolean hasDefaultGeneratorFor(Type t) {
            throw new UnsupportedOperationException("I heard you liked exceptions.");
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoGeneratorOfTheSpecifiedTypeExists() {
        repo = new TestRepo(Collections.<GeneratorHolder> emptyList());

        repo.getGeneratorFor(this.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoGeneratorOfTheSpecifiedNameExists() {
        repo = new TestRepo(Collections.<GeneratorHolder> emptyList());

        repo.getGeneratorFor("name");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldGetGeneratorForType() {
        Class<Integer> type = int.class;
        SampleGenerator generator = new SampleGenerator();
        GeneratorHolder holder = new GeneratorHolder(type, "a", generator);
        repo = new TestRepo(Arrays.asList(holder));

        assertThat(repo.hasGeneratorFor(type), is(true));
        assertThat(repo.getGeneratorFor(type), is((Generator) generator));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldGetGeneratorForName() {
        String name = "a";
        SampleGenerator generator = new SampleGenerator();
        GeneratorHolder holder = new GeneratorHolder(int.class, name, generator);
        repo = new TestRepo(Arrays.asList(holder));

        assertThat(repo.hasGeneratorFor(name), is(true));
        assertThat(repo.getGeneratorFor(name), is((Generator) generator));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptSeveralGeneratorsWithTheSameIdentifier() {
        String name = "a";
        SampleGenerator generator = new SampleGenerator();
        GeneratorHolder holder = new GeneratorHolder(int.class, name, generator);
        GeneratorHolder holder2 = new GeneratorHolder(int.class, name, generator);

        repo = new TestRepo(Arrays.asList(holder, holder2));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldReturnGeneratorOnRequestByNameIfSameGeneratorInstanceWasAddedUnderTheSameName() {
        String name = "a";
        SampleGenerator gen = new SampleGenerator();
        GeneratorHolder holder = new GeneratorHolder(int.class, name, gen);
        GeneratorHolder holder2 = new GeneratorHolder(double.class, name, gen);

        repo = new TestRepo(Arrays.asList(holder, holder2));

        assertThat(repo.hasGeneratorFor(name), is(true));
        assertThat(repo.getGeneratorFor(name), sameInstance((Generator) gen));
        Generator forDouble = repo.getGeneratorFor(double.class);
        Generator forInt = repo.getGeneratorFor(int.class);
        assertThat(forDouble, sameInstance(forInt));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldReturnASynthesizedGeneratorForTheGivenTypeIfComponentsExistAsGenerators() {
        Generator<?> gen = new SampleGenerator();
        GeneratorHolder holder = new GeneratorHolder(int.class, "a", gen);
        ParameterizedType iterableInt = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        RequestToSynthesize<Generator<?>> r = mock(RequestToSynthesize.class);
        given(r.synthesize(any(Synthesizer.class), any(GeneratorRepository.class))).willReturn((Generator) gen);

        repo = new TestRepo(Arrays.asList(holder));

        assertThat(repo.hasGeneratorFor(int.class), is(true));
        assertThat(repo.hasGeneratorFor(iterableInt), is(false));
        assertThat(repo.getSyntheticGeneratorFor(r), sameInstance((Generator) gen));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnRequestByNameIfSeveralGeneratorsExistForTheSameName() {
        String name = "a";
        // must be different instances of a generator
        GeneratorHolder holder = new GeneratorHolder(int.class, name, new SampleGenerator());
        GeneratorHolder holder2 = new GeneratorHolder(double.class, name, new SampleGenerator());

        repo = new TestRepo(Arrays.asList(holder, holder2));

        repo.hasGeneratorFor(name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnRequestByTypeIfSeveralGeneratorsExistForTheSameType() {
        // must be different instances of a generator
        GeneratorHolder holder = new GeneratorHolder(int.class, "a", new SampleGenerator());
        GeneratorHolder holder2 = new GeneratorHolder(int.class, "b", new SampleGenerator());

        repo = new TestRepo(Arrays.asList(holder, holder2));

        repo.hasGeneratorFor(int.class);
    }
}
