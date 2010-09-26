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

import lt.dm3.jquickcheck.api.LookupSynthetic;
import lt.dm3.jquickcheck.api.impl.lookup.DefaultLookupByName;
import lt.dm3.jquickcheck.api.impl.lookup.DefaultLookupByType;
import lt.dm3.jquickcheck.api.impl.resolution.NamedGenerator;
import lt.dm3.jquickcheck.api.impl.resolution.TypedGenerator;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.GeneratorHolder;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

public class DefaultGeneratorRepositoryTest {

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenQueriedForNameAndTypeHavingSeveralGeneratorsAsAResult() {
        String name = "a";
        Class<?> type = int.class;
        GeneratorHolder holder = new GeneratorHolder(type, name, new SampleGenerator());
        GeneratorHolder holder2 = new GeneratorHolder(type, name, new SampleGenerator());

        DefaultGeneratorRepository<Generator<?>> c = new DefaultGeneratorRepository<Generator<?>>(
                DefaultLookupByName.from(Arrays.asList((NamedGenerator<Generator<?>>) holder, holder2)),
                DefaultLookupByType.from(Arrays.asList((TypedGenerator<Generator<?>>) holder, holder2)),
                null, null);

        c.get(name, type);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldReturnASynthesizedGeneratorForTheGivenTypeIfComponentsExistAsGenerators() {
        Generator<?> gen = new SampleGenerator();
        GeneratorHolder holder = new GeneratorHolder(int.class, "a", gen);
        ParameterizedType iterableInt = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        LookupSynthetic<Generator<?>> synth = mock(LookupSynthetic.class);
        given(synth.getSynthetic(any(Type.class))).willReturn((Generator) gen);

        DefaultGeneratorRepository<Generator<?>> repo = new DefaultGeneratorRepository<Generator<?>>(
                DefaultLookupByName.from(Arrays.asList((NamedGenerator<Generator<?>>) holder)),
                DefaultLookupByType.from(Arrays.asList((TypedGenerator<Generator<?>>) holder)),
                null, synth);

        assertThat(repo.has(int.class), is(true));
        assertThat(repo.has(iterableInt), is(false));
        assertThat(repo.getSynthetic(iterableInt), sameInstance((Generator) gen));
    }

}
