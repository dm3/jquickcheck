package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Map;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.RequestToSynthesize;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultRequestToSynthesizeTest {

    private GeneratorRepository<Generator> repo;
    private Synthesizer<Generator> synth;
    private Settings settings;

    private RequestToSynthesize<Generator> request;

    @Before
    public void before() {
        repo = mock(GeneratorRepository.class);
        synth = mock(Synthesizer.class);
        settings = mock(Settings.class);
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfPrimitiveArrayType() {
        Generator gen = new SampleGenerator(), intGen = new IntegerGenerator();
        Type toSynthesize = int[].class;
        given(repo.hasGeneratorFor(int.class)).willReturn(true);
        given(repo.getGeneratorFor(int.class)).willReturn(intGen);
        given(synth.synthesize(toSynthesize, Arrays.asList(intGen))).willReturn(gen);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(synth, repo);

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfPrimitiveWrapperArrayType() {
        Generator gen = new SampleGenerator(), compGen = new SampleGenerator();
        Type toSynthesize = Object[].class;
        given(repo.hasGeneratorFor(Object.class)).willReturn(true);
        given(repo.getGeneratorFor(Object.class)).willReturn(compGen);
        given(synth.synthesize(toSynthesize, Arrays.asList(compGen))).willReturn(gen);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(synth, repo);

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfMultiDimensionalArrayType() {
        Generator gen = new SampleGenerator(), firstComponent = new SampleGenerator(), object = new SampleGenerator();
        Type toSynthesize = Object[][].class;
        given(repo.hasGeneratorFor(Object.class)).willReturn(true);
        given(repo.getGeneratorFor(Object.class)).willReturn(object);
        // first dimension
        given(synth.synthesize(Object[].class, Arrays.asList(object))).willReturn(firstComponent);
        // second dimension
        given(synth.synthesize(toSynthesize, Arrays.asList(firstComponent))).willReturn(gen);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(synth, repo);

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfGenericArrayType() {
        Generator gen = new SampleGenerator(), generator = new SampleGenerator(), object = new SampleGenerator();
        Type toSynthesize = new TypeToken<Generator<Object>[]>() {}.getType();
        given(repo.hasGeneratorFor(Object.class)).willReturn(true);
        given(repo.getGeneratorFor(Object.class)).willReturn(object);
        given(repo.hasGeneratorFor(Generator.class)).willReturn(true);
        given(repo.getGeneratorFor(Generator.class)).willReturn(generator);
        // step 1: Generator<Object>
        Type genOfObject = new TypeToken<Generator<Object>>() {}.getType();
        given(synth.synthesize(genOfObject, Arrays.asList(object))).willReturn(generator);
        // step 2: Generator<Object>[]
        given(synth.synthesize(toSynthesize, Arrays.asList(generator))).willReturn(gen);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(synth, repo);

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfOneLevelDepth() {
        Generator gen = new SampleGenerator();
        Generator genIterable = new SampleGenerator();
        Type integer = Integer.class;
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        given(repo.hasGeneratorFor(integer)).willReturn(true);
        given(repo.getGeneratorFor(integer)).willReturn(gen);
        given(synth.synthesize(toSynthesize, Arrays.asList(gen))).willReturn(genIterable);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(synth, repo);

        assertThat(result, sameInstance(genIterable));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfOneLevelDepthWithSeveralTypeParameters() {
        Generator gen = new SampleGenerator();
        Generator genMap = new SampleGenerator();
        Type integer = Integer.class;
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Map<Integer, Integer>>() {}.getType();
        given(repo.getGeneratorFor(integer)).willReturn(gen);
        given(synth.synthesize(toSynthesize, Arrays.asList(gen, gen))).willReturn(genMap);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(synth, repo);

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
        given(repo.getGeneratorFor(integer)).willReturn(gen);
        // first step - return an Iterable<Integer>
        given(synth.synthesize(intermediate, Arrays.asList(gen))).willReturn(genIterableInt);
        // second step - return an Iterable<Iterable<Integer>>
        given(synth.synthesize(toSynthesize, Arrays.asList(genIterableInt)))
                    .willReturn(genIterableIterable);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(synth, repo);

        assertThat(result, sameInstance(genIterableIterable));
    }

    @Test
    public void shouldGetTheComponentsFromADefaultGeneratorIfSpecifiedInSettings() {
        Generator gen = new SampleGenerator();
        Generator genIterable = new SampleGenerator();
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        given(repo.getDefaultGeneratorFor(Integer.class)).willReturn(gen);
        given(synth.synthesize(toSynthesize, Arrays.asList(gen))).willReturn(genIterable);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize,
                new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true, true));
        Generator result = request.synthesize(synth, repo);

        assertThat(result, sameInstance(genIterable));
    }

    @Test
    public void shouldGetTheComponentsFromANonDefaultGeneratorEvenIfDefaultIsTurnedOn() {
        Generator gen = new SampleGenerator();
        Generator genDefault = new SampleGenerator();
        Generator genIterable = new SampleGenerator();
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        Type intType = Integer.class;
        given(repo.hasGeneratorFor(intType)).willReturn(true);
        given(repo.getGeneratorFor(intType)).willReturn(gen);
        given(repo.getDefaultGeneratorFor(intType)).willReturn(genDefault);
        given(synth.synthesize(toSynthesize, Arrays.asList(gen))).willReturn(genIterable);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize,
                new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true, true));
        Generator result = request.synthesize(synth, repo);

        assertThat(result, sameInstance(genIterable));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfCreatedWithUnsupportedType_Class() {
        new DefaultRequestToSynthesize<Generator>(Integer.class, settings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfCreatedWithUnsupportedType_Wildcard() {
        new DefaultRequestToSynthesize<Generator>(new WildcardType() {
            @Override
            public Type[] getUpperBounds() {
                return null;
            }

            @Override
            public Type[] getLowerBounds() {
                return null;
            }
        }, settings);
    }

    @Test(expected = QuickCheckException.class)
    public void shouldFailIfNoComponentGeneratorExistsForAGivenRequestedTypeWhenDefaultsArentUsed() {
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        request.synthesize(synth, repo);
    }

    @Test(expected = QuickCheckException.class)
    public void shouldFailIfNoComponentGeneratorExistsForAGivenRequestedTypeWhenDefaultsAreUsed() {
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize,
                new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true, true));
        request.synthesize(synth, repo);
    }
}
