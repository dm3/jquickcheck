package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
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
import lt.dm3.jquickcheck.sample.IntegerGenerator;
import lt.dm3.jquickcheck.sample.Sample;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultSynthesizerTest {

    @Test
    public void shouldSynthesizeAGeneratorForThePrimitiveArrayTypeIfASynthesizedForThatTypeExists() {
        final Generator gen = new SampleGenerator(), intGen = new IntegerGenerator();
        Synthesized<Generator> s = new DefaultSynthesizer.AbstractSynthesized<Generator>(int[].class) {
            @Override
            public Generator synthesize(List<Generator> components) {
                return gen;
            }
        };
        Synthesizer<Generator> synth = new DefaultSynthesizer<Generator>(Arrays.asList(s));

        Type type = int[].class;
        Generator result = synth.synthesize(type, Arrays.asList(intGen));

        assertThat(result, sameInstance(gen));
    }

    @Test
    public void shouldSynthesizeAGeneratorForTheNonPrimitiveNonWrapperArrayTypeIfASynthesizedForThatTypeExists() {
        final Generator gen = new SampleGenerator(), sampleGen = new SampleGenerator();
        Synthesized<Generator> s = new DefaultSynthesizer.AbstractSynthesized<Generator>(Sample[].class) {
            @Override
            public Generator synthesize(List<Generator> components) {
                assertThat(components.get(0), is(sampleGen));
                return gen;
            }
        };
        Synthesizer<Generator> synth = new DefaultSynthesizer<Generator>(Arrays.asList(s));

        Type type = Sample[].class;
        Generator result = synth.synthesize(type, Arrays.asList(sampleGen));

        assertThat(result, sameInstance(gen));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfGeneratingNonPrimitiveNonWrapperArrayTypeIfASynthesizedForThatTypeDoesNotExist() {
        final Generator gen = new SampleGenerator();
        Synthesized<Generator> s = new DefaultSynthesizer.AbstractSynthesized<Generator>(Sample[].class) {
            @Override
            public Generator synthesize(List<Generator> components) {
                return gen;
            }
        };
        Synthesizer<Generator> synth = new DefaultSynthesizer<Generator>(Arrays.asList(s));

        Type type = Sample[].class;
        synth.synthesize(type, Collections.<Generator> emptyList());
    }

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

        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        Generator result = synth.synthesize(type, Arrays.asList(intGen));

        assertThat(result, sameInstance(gen));
    }

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

        Type type = new TypeToken<List<Integer>>() {}.getType();
        Generator result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(gen));

        type = new TypeToken<Collection<Integer>>() {}.getType();
        result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(gen));

        type = new TypeToken<Iterable<Integer>>() {}.getType();
        result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(gen));
    }

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
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        Generator result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(arrayListGen));

        // Iterable -> ArrayList, as it came first in the list of synthesizeds
        type = new TypeToken<Iterable<Integer>>() {}.getType();
        result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(arrayListGen));

        // LinkedList -> LinkedList
        type = new TypeToken<LinkedList<Integer>>() {}.getType();
        result = synth.synthesize(type, Arrays.asList(intGen));
        assertThat(result, sameInstance(linkedListGen));
    }

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
        Type type = new TypeToken<LinkedList<Integer>>() {}.getType();

        synth.synthesize(type, Collections.<Generator> emptyList());
    }

    @Test(expected = QuickCheckException.class)
    public void shouldThrowAnExceptionIfNoGeneratorCouldBeSynthesizedForTheGivenType() {
        Synthesizer<Generator> synth = new DefaultSynthesizer<Generator>(
                Collections.<Synthesized<Generator>> emptyList());
        Type type = new TypeToken<Iterable<Integer>>() {}.getType();

        synth.synthesize(type, (List) Arrays.asList(new SampleGenerator()));
    }
}
