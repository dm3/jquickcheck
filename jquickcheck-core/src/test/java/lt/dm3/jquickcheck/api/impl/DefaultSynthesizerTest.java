package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.api.impl.DefaultSynthesizer.Synthesized;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

public class DefaultSynthesizerTest {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldSynthesizeAGeneratorForTheGivenTypeIfASynthesizedForThatTypeExists() {
        final Generator gen = new SampleGenerator(), intGen = new SampleGenerator();
        Synthesized<Generator> s = new DefaultSynthesizer.AbstractSynthesized<Generator>(ArrayList.class) {
            @Override
            public Generator synthesize(List<Generator> components) {
                assertThat(components.get(0), is(intGen));
                return gen;
            }
        };
        Synthesizer<Generator> synth = new DefaultSynthesizer<Generator>(Arrays.asList(s));

        ParameterizedType type = (ParameterizedType) new TypeToken<ArrayList<Integer>>() {}.getType();
        Generator result = synth.synthesize(type, Arrays.asList(intGen));

        assertThat(result, sameInstance(gen));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldSynthesizeAGeneratorForTheGivenTypeIfASynthesizedForTheSubTypeExists() {
        final Generator gen = new SampleGenerator(), intGen = new SampleGenerator();
        Synthesized<Generator> s = new DefaultSynthesizer.AbstractSynthesized<Generator>(ArrayList.class) {
            @Override
            public Generator synthesize(List<Generator> components) {
                assertThat(components.get(0), is(intGen));
                return gen;
            }
        };
        Synthesizer<Generator> synth = new DefaultSynthesizer<Generator>(Arrays.asList(s));

        ParameterizedType type = (ParameterizedType) new TypeToken<List<Integer>>() {}.getType();
        Generator result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(gen));

        type = (ParameterizedType) new TypeToken<Collection<Integer>>() {}.getType();
        result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(gen));

        type = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(gen));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldNotOverwriteSynthesizedsWhenSeveralFoundForOneType() {
        final Generator arrayListGen = new SampleGenerator(), linkedListGen = new SampleGenerator(), intGen = new SampleGenerator();
        Synthesized<Generator> arrayListS = new DefaultSynthesizer.AbstractSynthesized<Generator>(ArrayList.class) {
            @Override
            public Generator synthesize(List<Generator> components) {
                assertThat(components.get(0), is(intGen));
                return arrayListGen;
            }
        };
        Synthesized<Generator> linkLists = new DefaultSynthesizer.AbstractSynthesized<Generator>(LinkedList.class) {
            @Override
            public Generator synthesize(List<Generator> components) {
                assertThat(components.get(0), is(intGen));
                return linkedListGen;
            }
        };
        Synthesizer<Generator> synth = new DefaultSynthesizer<Generator>(Arrays.asList(arrayListS, linkLists));

        // ArrayList -> ArrayList
        ParameterizedType type = (ParameterizedType) new TypeToken<ArrayList<Integer>>() {}.getType();
        Generator result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(arrayListGen));

        // Iterable -> ArrayList, as it came first in the list of synthesizeds
        type = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();
        result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(arrayListGen));

        // LinkedList -> LinkedList
        type = (ParameterizedType) new TypeToken<LinkedList<Integer>>() {}.getType();
        result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(linkedListGen));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionIfNotEnoughGeneratorsProvided() {
        final Generator arrayListGen = new SampleGenerator();
        Synthesized<Generator> arrayListS = new DefaultSynthesizer.AbstractSynthesized<Generator>(ArrayList.class) {
            @Override
            public Generator synthesize(List<Generator> components) {
                return arrayListGen;
            }
        };
        Synthesizer<Generator> synth = new DefaultSynthesizer<Generator>(Arrays.asList(arrayListS));
        ParameterizedType type = (ParameterizedType) new TypeToken<LinkedList<Integer>>() {}.getType();

        synth.synthesize(type, Collections.<Generator> emptyList());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(expected = QuickCheckException.class)
    public void shouldThrowAnExceptionIfNoGeneratorCouldBeSynthesizedForTheGivenType() {
        Synthesizer<Generator> synth = new DefaultSynthesizer<Generator>(
                Collections.<Synthesized<Generator>> emptyList());
        ParameterizedType type = (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType();

        synth.synthesize(type, (List) Arrays.asList(new SampleGenerator()));
    }
}
