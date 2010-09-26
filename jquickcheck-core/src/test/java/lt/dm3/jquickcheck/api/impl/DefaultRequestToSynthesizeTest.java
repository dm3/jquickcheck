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
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultRequestToSynthesizeTest {

    private GeneratorRepository<Generator> repo;
    private Settings settings;

    private RequestToSynthesize<Generator> request;

    @Before
    public void before() {
        repo = mock(GeneratorRepository.class);
        settings = mock(Settings.class);
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfPrimitiveArrayType() {
        Generator gen = new SampleGenerator(), intGen = new IntegerGenerator();
        Type toSynthesize = int[].class;
        given(repo.has(int.class)).willReturn(true);
        given(repo.get(int.class)).willReturn(intGen);
        given(repo.getSyntheticGeneratorFor(toSynthesize, Arrays.asList(intGen))).willReturn(gen);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(repo);

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfPrimitiveWrapperArrayType() {
        Generator gen = new SampleGenerator(), compGen = new SampleGenerator();
        Type toSynthesize = Object[].class;
        given(repo.has(Object.class)).willReturn(true);
        given(repo.get(Object.class)).willReturn(compGen);
        given(repo.getSyntheticGeneratorFor(toSynthesize, Arrays.asList(compGen))).willReturn(gen);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(repo);

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldCreateASyntheticGeneratorOfMultiDimensionalArrayType() {
        Generator gen = new SampleGenerator(), firstComponent = new SampleGenerator(), object = new SampleGenerator();
        Type toSynthesize = Object[][].class;
        given(repo.has(Object.class)).willReturn(true);
        given(repo.get(Object.class)).willReturn(object);
        // first dimension
        given(repo.getSyntheticGeneratorFor(Object[].class, Arrays.asList(object))).willReturn(firstComponent);
        // second dimension
        given(repo.getSyntheticGeneratorFor(toSynthesize, Arrays.asList(firstComponent))).willReturn(gen);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(repo);

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
        given(repo.getSyntheticGeneratorFor(genOfObject, Arrays.asList(object))).willReturn(generator);
        // step 2: Generator<Object>[]
        given(repo.getSyntheticGeneratorFor(toSynthesize, Arrays.asList(generator))).willReturn(gen);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(repo);

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
        given(repo.getSyntheticGeneratorFor(toSynthesize, Arrays.asList(gen))).willReturn(genIterable);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(repo);

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
        given(repo.getSyntheticGeneratorFor(toSynthesize, Arrays.asList(gen, gen))).willReturn(genMap);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(repo);

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
        given(repo.getSyntheticGeneratorFor(intermediate, Arrays.asList(gen))).willReturn(genIterableInt);
        // second step - return an Iterable<Iterable<Integer>>
        given(repo.getSyntheticGeneratorFor(toSynthesize, Arrays.asList(genIterableInt)))
                    .willReturn(genIterableIterable);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize, settings);
        Generator result = request.synthesize(repo);

        assertThat(result, sameInstance(genIterableIterable));
    }

    @Test
    public void shouldGetTheComponentsFromADefaultGeneratorIfSpecifiedInSettings() {
        Generator gen = new SampleGenerator();
        Generator genIterable = new SampleGenerator();
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        given(repo.hasDefault(Integer.class)).willReturn(true);
        given(repo.getDefault(Integer.class)).willReturn(gen);
        given(repo.getSyntheticGeneratorFor(toSynthesize, Arrays.asList(gen))).willReturn(genIterable);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize,
                new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true, true));
        Generator result = request.synthesize(repo);

        assertThat(result, sameInstance(genIterable));
    }

    @Test
    public void shouldGetTheComponentsFromANonDefaultGeneratorEvenIfDefaultIsTurnedOn() {
        Generator gen = new SampleGenerator();
        Generator genDefault = new SampleGenerator();
        Generator genIterable = new SampleGenerator();
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        Type intType = Integer.class;
        given(repo.has(intType)).willReturn(true);
        given(repo.get(intType)).willReturn(gen);
        given(repo.getDefault(intType)).willReturn(genDefault);
        given(repo.getSyntheticGeneratorFor(toSynthesize, Arrays.asList(gen))).willReturn(genIterable);

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize,
                new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true, true));
        Generator result = request.synthesize(repo);

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
        request.synthesize(repo);
    }

    @Test(expected = QuickCheckException.class)
    public void shouldFailIfNoComponentGeneratorExistsForAGivenRequestedTypeWhenDefaultsAreUsed() {
        ParameterizedType toSynthesize = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();

        request = new DefaultRequestToSynthesize<Generator>(toSynthesize,
                new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true, true));
        request.synthesize(repo);
    }

}
