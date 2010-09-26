package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.Sample;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultPropertyParameterTest {

    GeneratorRepository<Generator> repo;

    @Before
    public void before() {
        repo = mock(GeneratorRepository.class);
    }

    @Test
    public void shouldGetTheNamedGeneratorFromTheRepository() {
        String name = "a";
        Generator generator = new SampleGenerator();
        given(repo.get(any(Type.class))).willThrow(new IllegalArgumentException());
        given(repo.getDefault(any(Type.class))).willThrow(new IllegalArgumentException());
        given(repo.has(name)).willReturn(true);
        given(repo.get(anyString())).willReturn(generator);
        G ann = mock(G.class);
        given(ann.gen()).willReturn(name);

        Generator result = defaultParameter(Sample.class, ann).getGeneratorFrom(repo);

        assertThat(result, is(generator));
    }

    @Test
    public void shouldGetTheGeneratorForTheTypeFromTheRepositoryIfDefaultExists() {
        Type type = Sample.class;
        Generator generator = new SampleGenerator();
        given(repo.get(anyString())).willThrow(new IllegalArgumentException());
        given(repo.hasDefault(type)).willReturn(true);
        given(repo.has(type)).willReturn(true);
        given(repo.get(type)).willReturn(generator);

        Generator<Sample> result = defaultParameter(type).getGeneratorFrom(repo);

        assertThat(result, is(generator));
    }

    @Test
    public void shouldGetTheGeneratorForTheTypeFromTheRepository() {
        Type type = Sample.class;
        Generator generator = new SampleGenerator();
        given(repo.get(anyString())).willThrow(new IllegalArgumentException());
        given(repo.getDefault(any(Type.class))).willThrow(new IllegalArgumentException());
        given(repo.has(type)).willReturn(true);
        given(repo.get(type)).willReturn(generator);

        Generator<Sample> result = defaultParameter(type).getGeneratorFrom(repo);

        assertThat(result, is(generator));
    }

    @Test
    public void shouldGetTheDefaultGeneratorIfNoGeneratorForNameOrTypeFound() {
        Class<Sample> type = Sample.class;
        Generator<Sample> generator = new SampleGenerator();
        given(repo.get(anyString())).willThrow(new IllegalArgumentException());
        given(repo.get(any(Type.class))).willThrow(new IllegalArgumentException());
        given(repo.hasDefault(type)).willReturn(true);
        given(repo.getDefault(type)).willReturn(generator);

        Generator<Sample> result = defaultParameter(type).getGeneratorFrom(repo);

        assertThat(result, is(generator));
    }

    @Test
    public void shouldGetTheNamedGeneratorEvenIfTheTypedGeneratorExists() {
        Type type = Sample.class;
        String name = "a";
        Generator<Sample> generatorForType = new SampleGenerator();
        Generator<Sample> generatorForName = new SampleGenerator();
        given(repo.has(type)).willReturn(true);
        given(repo.get(type)).willReturn(generatorForType);
        given(repo.has(name)).willReturn(true);
        given(repo.get(name)).willReturn(generatorForName);
        G ann = mock(G.class);
        given(ann.gen()).willReturn(name);

        Generator<Sample> result = defaultParameter(type, ann).getGeneratorFrom(repo);

        assertThat(result, is(generatorForName));
    }

    @Test
    public void shouldSynthesizeAGeneratorIfSettingsSaySoAndNoGeneratorFoundInNonDefaultsOrDefaults() {
        ParameterizedType type = (ParameterizedType) new TypeToken<List<Sample>>() {}.getType();
        Generator sampleGen = new SampleGenerator(), gen = new SampleGenerator();
        GeneratorRepository<Generator> repo = mock(GeneratorRepository.class);
        given(repo.get(anyString())).willThrow(new IllegalArgumentException());
        given(repo.has(Sample.class)).willReturn(true);
        given(repo.get(Sample.class)).willReturn(sampleGen);
        given(repo.getDefault(any(Type.class))).willThrow(new IllegalArgumentException());
        given(repo.getSynthetic(type, Arrays.asList(sampleGen))).willReturn(gen);

        Generator result = defaultParameter(type).getGeneratorFrom(repo);

        assertThat(result, sameInstance(gen));
    }

    @Test(expected = QuickCheckException.class)
    public void shouldThrowExceptionIfGeneratorNameIsSpecifiedButGeneratorIsNotFoundInTheRepo() {
        Class<Sample> type = Sample.class;
        Generator<Sample> generatorForType = new SampleGenerator();
        given(repo.get(anyString())).willThrow(new IllegalArgumentException());
        given(repo.getDefault(any(Type.class))).willThrow(new IllegalArgumentException());
        given(repo.has(type)).willReturn(true);
        given(repo.get(type)).willReturn(generatorForType);

        String name = "a";
        G ann = mock(G.class);
        given(ann.gen()).willReturn(name);

        defaultParameter(type, ann).getGeneratorFrom(repo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfGeneratorNotFoundNeitherInDefaultsOrSyntheticsOrNormalGenerators() {
        Class<Sample> type = Sample.class;

        // synthetics are not applicable as type is non-parameterized
        defaultParameter(type).getGeneratorFrom(repo);
    }

    private DefaultPropertyParameter<Generator> defaultParameter(Type t, G... ann) {
        // USE DEFAULTS = true, USE SYNTHETICS = true
        return new DefaultPropertyParameter<Generator>(t, ann, new DefaultInvocationSettings(1, true, true));
    }
}
