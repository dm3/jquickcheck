package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.RequestToSynthesize;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

public class DefaultRequestToSynthesizeTest {

    @SuppressWarnings("rawtypes")
    private GeneratorRepository<Generator> repo;
    @SuppressWarnings("rawtypes")
    private Synthesizer<Generator> synth;

    @SuppressWarnings("rawtypes")
    private RequestToSynthesize<Generator> request;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void before() {
        repo = mock(GeneratorRepository.class);
        synth = mock(Synthesizer.class);

        request = new DefaultRequestToSynthesize<Generator>(new DefaultInvocationSettings());
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void shouldCreateASyntheticGeneratorOfOneLevelDepth() {
        Generator gen = new SampleGenerator();
        Generator genIterable = new SampleGenerator();
        Type integer = Integer.class;
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        given(repo.getGeneratorFor(integer)).willReturn(gen);
        given(synth.synthesize(toSynthesize, Arrays.asList(gen))).willReturn(genIterable);

        Generator result = request.synthesize(toSynthesize, synth, repo);

        assertThat(result, sameInstance(genIterable));
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void shouldCreateASyntheticGeneratorOfOneLevelDepthWithSeveralTypeParameters() {
        Generator gen = new SampleGenerator();
        Generator genMap = new SampleGenerator();
        Type integer = Integer.class;
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Map<Integer, Integer>>() {}.getType();
        given(repo.getGeneratorFor(integer)).willReturn(gen);
        given(synth.synthesize(toSynthesize, Arrays.asList(gen, gen))).willReturn(genMap);

        Generator result = request.synthesize(toSynthesize, synth, repo);

        assertThat(result, sameInstance(genMap));
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void shouldCreateASyntheticGeneratorOfSeveralLevelsDepth() {
        Generator gen = new SampleGenerator();
        Generator genIterableInt = new SampleGenerator();
        Generator genIterableIterable = new SampleGenerator();
        Type integer = Integer.class;
        ParameterizedType intermediate = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Iterable<Integer>>>() {}.getType();
        given(repo.getGeneratorFor(integer)).willReturn(gen);
        // first step - return an Iterable<Integer>
        given(synth.synthesize(intermediate, Arrays.asList(gen))).willReturn(genIterableInt);
        // second step - return an Iterable<Iterable<Integer>>
        given(synth.synthesize(toSynthesize, Arrays.asList(genIterableInt)))
                    .willReturn(genIterableIterable);

        Generator result = request.synthesize(toSynthesize, synth, repo);

        assertThat(result, sameInstance(genIterableIterable));
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void shouldGetTheComponentsFromADefaultGeneratorIfSpecifiedInSettings() {
        request = new DefaultRequestToSynthesize<Generator>(
                new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true, true));
        Generator gen = new SampleGenerator();
        Generator genIterable = new SampleGenerator();
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        given(repo.getDefaultGeneratorFor(Integer.class)).willReturn(gen);
        given(synth.synthesize(toSynthesize, Arrays.asList(gen))).willReturn(genIterable);

        Generator result = request.synthesize(toSynthesize, synth, repo);

        assertThat(result, sameInstance(genIterable));
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void shouldGetTheComponentsFromANonDefaultGeneratorEvenIfDefaultIsTurnedOn() {
        request = new DefaultRequestToSynthesize<Generator>(
                new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true, true));
        Generator gen = new SampleGenerator();
        Generator genDefault = new SampleGenerator();
        Generator genIterable = new SampleGenerator();
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        Type intType = Integer.class;
        given(repo.hasGeneratorFor(intType)).willReturn(true);
        given(repo.getGeneratorFor(intType)).willReturn(gen);
        given(repo.getDefaultGeneratorFor(intType)).willReturn(genDefault);
        given(synth.synthesize(toSynthesize, Arrays.asList(gen))).willReturn(genIterable);

        Generator result = request.synthesize(toSynthesize, synth, repo);

        assertThat(result, sameInstance(genIterable));
    }

    @Test(expected = QuickCheckException.class)
    public void shouldFailIfNoComponentGeneratorExistsForAGivenRequestedTypeWhenDefaultsArentUsed() {
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();

        request.synthesize(toSynthesize, synth, repo);
    }

    @SuppressWarnings("rawtypes")
    @Test(expected = QuickCheckException.class)
    public void shouldFailIfNoComponentGeneratorExistsForAGivenRequestedTypeWhenDefaultsAreUsed() {
        request = new DefaultRequestToSynthesize<Generator>(
                new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true, true));
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();

        request.synthesize(toSynthesize, synth, repo);
    }
}
