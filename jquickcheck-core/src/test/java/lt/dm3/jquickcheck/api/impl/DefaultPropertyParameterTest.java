package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.RequestToSynthesize;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.Sample;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

public class DefaultPropertyParameterTest {

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetTheNamedGeneratorFromTheRepository() {
        String name = "a";
        Generator<Sample> generator = new SampleGenerator();
        GeneratorRepository<Generator<Sample>> repo = mock(GeneratorRepository.class);
        given(repo.hasGeneratorFor(name)).willReturn(true);
        given(repo.getGeneratorFor(name)).willReturn(generator);
        G ann = mock(G.class);
        given(ann.gen()).willReturn(name);

        Generator<Sample> result = defaultParameter(Sample.class, ann).getGeneratorFrom(repo);

        assertThat(result, is(generator));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetTheGeneratorForTheTypeFromTheRepository() {
        Type type = Sample.class;
        Generator<Sample> generator = new SampleGenerator();
        GeneratorRepository<Generator<Sample>> repo = mock(GeneratorRepository.class);
        given(repo.hasGeneratorFor(type)).willReturn(true);
        given(repo.getGeneratorFor(type)).willReturn(generator);

        Generator<Sample> result = defaultParameter(type).getGeneratorFrom(repo);

        assertThat(result, is(generator));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetTheDefaultGeneratorIfNoGeneratorForNameOrTypeFound() {
        Class<Sample> type = Sample.class;
        Generator<Sample> generator = new SampleGenerator();
        GeneratorRepository<Generator<Sample>> repo = mock(GeneratorRepository.class);
        given(repo.getDefaultGeneratorFor(type)).willReturn(generator);

        Generator<Sample> result = defaultParameter(type).getGeneratorFrom(repo);

        assertThat(result, is(generator));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetTheNamedGeneratorEvenIfTheTypedGeneratorExists() {
        Type type = Sample.class;
        String name = "a";
        Generator<Sample> generatorForType = new SampleGenerator();
        Generator<Sample> generatorForName = new SampleGenerator();
        GeneratorRepository<Generator<Sample>> repo = mock(GeneratorRepository.class);
        given(repo.hasGeneratorFor(type)).willReturn(true);
        given(repo.getGeneratorFor(type)).willReturn(generatorForType);
        given(repo.hasGeneratorFor(name)).willReturn(true);
        given(repo.getGeneratorFor(name)).willReturn(generatorForName);
        G ann = mock(G.class);
        given(ann.gen()).willReturn(name);

        Generator<Sample> result = defaultParameter(type, ann).getGeneratorFrom(repo);

        assertThat(result, is(generatorForName));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldSynthesizeAGeneratorIfSettingsSaySoAndNoGeneratorFoundInNonDefaultsOrDefaults() {
        ParameterizedType type = (ParameterizedType) new TypeToken<List<Sample>>() {}.getType();
        Generator gen = new SampleGenerator();
        GeneratorRepository<Generator> repo = mock(GeneratorRepository.class);
        given(repo.hasGeneratorFor(type)).willReturn(false);
        given(repo.getSyntheticGeneratorFor(eq(type), any(RequestToSynthesize.class))).willReturn(gen);

        Generator result = defaultGenericParameter(type).getGeneratorFrom(repo);

        assertThat(result, sameInstance(gen));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = QuickCheckException.class)
    public void shouldThrowExceptionIfGeneratorNameIsSpecifiedButGeneratorIsNotFoundInTheRepo() {
        Class<Sample> type = Sample.class;
        Generator<Sample> generatorForType = new SampleGenerator();
        GeneratorRepository<Generator<Sample>> repo = mock(GeneratorRepository.class);
        given(repo.hasGeneratorFor(type)).willReturn(true);
        given(repo.getGeneratorFor(type)).willReturn(generatorForType);

        String name = "a";
        G ann = mock(G.class);
        given(ann.gen()).willReturn(name);

        defaultParameter(type, ann).getGeneratorFrom(repo);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = QuickCheckException.class)
    public void shouldThrowExceptionIfGeneratorNotFoundNeitherInDefaultsOrSyntheticsOrNormalGenerators() {
        Class<Sample> type = Sample.class;
        GeneratorRepository<Generator<Sample>> repo = mock(GeneratorRepository.class);

        // synthetics are not applicable as type is non-parameterized
        defaultParameter(type).getGeneratorFrom(repo);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private DefaultPropertyParameter<Generator> defaultGenericParameter(Type t, G... ann) {
        return (DefaultPropertyParameter) defaultParameter(t, ann);
    }

    private DefaultPropertyParameter<Generator<Sample>> defaultParameter(Type t, G... ann) {
        // USE DEFAULTS = true, USE SYNTHETICS = true
        return new DefaultPropertyParameter<Generator<Sample>>(t, ann, new DefaultInvocationSettings(1, true, true));
    }
}
