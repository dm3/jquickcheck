package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.LookupSynthetic;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultLookupSyntheticTest {

    private Lookup<Type, Generator> repo;
    private Synthesizer<Generator> synth;

    private LookupSynthetic<Generator> lookup;

    @Before
    public void before() {
        repo = mock(Lookup.class);
        synth = mock(Synthesizer.class);
        lookup = new DefaultLookupSynthetic<Generator>(synth, repo);
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfPrimitiveArrayType() {
        Generator gen = new SampleGenerator(), intGen = new IntegerGenerator();
        Type toSynthesize = int[].class;
        given(repo.has(int.class)).willReturn(true);
        given(repo.get(int.class)).willReturn(intGen);
        given(synth.synthesize(toSynthesize, Arrays.asList(intGen))).willReturn(gen);

        assertThat(lookup.hasSynthetic(toSynthesize), is(true));
        Generator result = lookup.getSynthetic(toSynthesize);

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfPrimitiveWrapperArrayType() {
        Generator gen = new SampleGenerator(), compGen = new SampleGenerator();
        Type toSynthesize = Object[].class;
        given(repo.has(Object.class)).willReturn(true);
        given(repo.get(Object.class)).willReturn(compGen);
        given(synth.synthesize(toSynthesize, Arrays.asList(compGen))).willReturn(gen);

        assertThat(lookup.hasSynthetic(toSynthesize), is(true));
        Generator result = lookup.getSynthetic(toSynthesize);

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfMultiDimensionalArrayType() {
        Generator gen = new SampleGenerator(), firstComponent = new SampleGenerator(), object = new SampleGenerator();
        Type toSynthesize = Object[][].class;
        given(repo.has(Object.class)).willReturn(true);
        given(repo.get(Object.class)).willReturn(object);
        // first dimension
        given(synth.synthesize(Object[].class, Arrays.asList(object))).willReturn(firstComponent);
        // second dimension
        given(synth.synthesize(toSynthesize, Arrays.asList(firstComponent))).willReturn(gen);

        assertThat(lookup.hasSynthetic(toSynthesize), is(true));
        Generator result = lookup.getSynthetic(toSynthesize);

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfGenericArrayType() {
        Generator gen = new SampleGenerator(), generator = new SampleGenerator(), object = new SampleGenerator();
        Type toSynthesize = new TypeToken<Generator<Object>[]>() {}.getType();
        given(repo.has(Object.class)).willReturn(true);
        given(repo.get(Object.class)).willReturn(object);
        given(repo.has(Generator.class)).willReturn(true);
        given(repo.get(Generator.class)).willReturn(generator);
        // step 1: Generator<Object>
        Type genOfObject = new TypeToken<Generator<Object>>() {}.getType();
        given(synth.synthesize(genOfObject, Arrays.asList(object))).willReturn(generator);
        // step 2: Generator<Object>[]
        given(synth.synthesize(toSynthesize, Arrays.asList(generator))).willReturn(gen);

        assertThat(lookup.hasSynthetic(toSynthesize), is(true));
        Generator result = lookup.getSynthetic(toSynthesize);

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfOneLevelDepth() {
        Generator gen = new SampleGenerator();
        Generator genIterable = new SampleGenerator();
        Type integer = Integer.class;
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        given(repo.has(integer)).willReturn(true);
        given(repo.get(integer)).willReturn(gen);
        given(synth.synthesize(toSynthesize, Arrays.asList(gen))).willReturn(genIterable);

        assertThat(lookup.hasSynthetic(toSynthesize), is(true));
        Generator result = lookup.getSynthetic(toSynthesize);

        assertThat(result, sameInstance(genIterable));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfOneLevelDepthWithSeveralTypeParameters() {
        Generator gen = new SampleGenerator();
        Generator genMap = new SampleGenerator();
        Type integer = Integer.class;
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Map<Integer, Integer>>() {}.getType();
        given(repo.has(integer)).willReturn(true);
        given(repo.get(integer)).willReturn(gen);
        given(synth.synthesize(toSynthesize, Arrays.asList(gen, gen))).willReturn(genMap);

        assertThat(lookup.hasSynthetic(toSynthesize), is(true));
        Generator result = lookup.getSynthetic(toSynthesize);

        assertThat(result, sameInstance(genMap));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfSeveralLevelsDepth() {
        Generator gen = new SampleGenerator();
        Generator genIterableInt = new SampleGenerator();
        Generator genIterableIterable = new SampleGenerator();
        Type integer = Integer.class;
        ParameterizedType intermediate = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Iterable<Integer>>>() {}.getType();
        given(repo.has(integer)).willReturn(true);
        given(repo.get(integer)).willReturn(gen);
        // first step - return an Iterable<Integer>
        given(synth.synthesize(intermediate, Arrays.asList(gen))).willReturn(genIterableInt);
        // second step - return an Iterable<Iterable<Integer>>
        given(synth.synthesize(toSynthesize, Arrays.asList(genIterableInt)))
                    .willReturn(genIterableIterable);

        assertThat(lookup.hasSynthetic(toSynthesize), is(true));
        Generator result = lookup.getSynthetic(toSynthesize);

        assertThat(result, sameInstance(genIterableIterable));
    }

}
