package lt.dm3.jquickcheck.fj;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.api.impl.DefaultRequestToSynthesize;
import lt.dm3.jquickcheck.api.impl.DefaultSynthesizer;
import lt.dm3.jquickcheck.api.impl.DefaultSynthesizer.AbstractSynthesized;
import lt.dm3.jquickcheck.api.impl.DefaultSynthesizer.Synthesized;
import lt.dm3.jquickcheck.api.impl.NamedAndTypedGenerator;

import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

import fj.test.Arbitrary;
import fj.test.Rand;

public class FJGeneratorRepositoryTest {

    private static class FJGenHolder implements NamedAndTypedGenerator<Arbitrary<?>> {

        private final Type type;
        private final String name;
        private final Arbitrary<?> generator;

        public FJGenHolder(Type type, String name, Arbitrary<?> generator) {
            this.type = type;
            this.name = name;
            this.generator = generator;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Arbitrary<?> getGenerator() {
            return generator;
        }

    }

    @Test
    public void shouldCreateASyntheticGenerator() {
        FJGenHolder h = new FJGenHolder(int.class, "a", Arbitrary.arbInteger);
        Synthesized<Arbitrary<?>> s = new AbstractSynthesized<Arbitrary<?>>(ArrayList.class) {
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbArrayList(components.get(0));
            }
        };
        Synthesizer<Arbitrary<?>> synth = new DefaultSynthesizer<Arbitrary<?>>(Arrays.asList(s));
        GeneratorRepository<Arbitrary<?>> repo = new FJGeneratorRepository((Iterable) Arrays.asList(h), synth);

        Arbitrary<?> result = repo.getSyntheticGeneratorFor(
                (ParameterizedType) new TypeToken<ArrayList<Integer>>() {}.getType(),
                new DefaultRequestToSynthesize<Arbitrary<?>>(mock(Settings.class)));

        assertThat(result, not(nullValue()));
        assertThat(result.gen.gen(1, Rand.standard), instanceOf(ArrayList.class));

        result = repo.getSyntheticGeneratorFor(
                (ParameterizedType) new TypeToken<Iterable<Integer>>() {}.getType(),
                new DefaultRequestToSynthesize<Arbitrary<?>>(mock(Settings.class)));

        assertThat(result, not(nullValue()));
        assertThat(result.gen.gen(1, Rand.standard), instanceOf(ArrayList.class));
    }
}
