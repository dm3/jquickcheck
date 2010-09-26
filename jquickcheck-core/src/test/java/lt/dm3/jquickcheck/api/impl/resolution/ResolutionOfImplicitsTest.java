package lt.dm3.jquickcheck.api.impl.resolution;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.impl.resolution.GeneratorResolutionStrategyTest.TestResolutionOfImplicits;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ResolutionOfImplicitsTest {

    private final ResolutionOfImplicits<Generator<?>> resolution = new TestResolutionOfImplicits();

    static class NoDependencies {
        @G
        public String stringGen(int x) {
            return "a";
        }
    }

    @Test
    public void shouldResolveImplicitsWithNoDependencies() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        given(repo.has(int.class)).willReturn(true);
        IntegerGenerator intGen = new IntegerGenerator();
        given(repo.get(int.class)).willReturn((Generator) intGen);

        List<NamedAndTypedGenerator<Generator<?>>> result = toList(resolution.resolveFrom(new NoDependencies(), repo));

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getName(), equalTo("stringGen"));
        assertThat(result.get(0).getType(), equalTo((Type) String.class));
    }

    @Test
    public void shouldResolveImplicitsWithDefaultDependencies() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        given(repo.hasDefault(int.class)).willReturn(true);
        IntegerGenerator intGen = new IntegerGenerator();
        given(repo.getDefault(int.class)).willReturn((Generator) intGen);

        List<NamedAndTypedGenerator<Generator<?>>> result = toList(resolution.resolveFrom(new NoDependencies(), repo));

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getName(), equalTo("stringGen"));
        assertThat(result.get(0).getType(), equalTo((Type) String.class));
    }

    static class SimpleDependencies {
        @G
        public String stringGen(int x) {
            return "a";
        }

        @G
        public int intGen() {
            return 1;
        }
    }

    @Test
    public void shouldResolveImplicitsWithSimpleDependencies() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        List<NamedAndTypedGenerator<Generator<?>>> result = toList(resolution.resolveFrom(new SimpleDependencies(),
                repo));

        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getName(), equalTo("intGen"));
        assertThat(result.get(0).getType(), equalTo((Type) int.class));
        assertThat(result.get(1).getName(), equalTo("stringGen"));
        assertThat(result.get(1).getType(), equalTo((Type) String.class));
    }

    static class SimpleSyntheticDependencies {
        @G
        public String stringGen(List<Integer> x) {
            return "a";
        }

        @G
        public int intGen() {
            return 1;
        }
    }

    @Test
    public void shouldResolveImplicitsWithSimpleSyntheticDependencies() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        Generator<?> gen = new SampleGenerator();
        given(repo.hasSynthetic(List.class)).willReturn(true);
        given(repo.getSynthetic(eq(List.class), anyList())).willReturn((Generator) gen);

        List<NamedAndTypedGenerator<Generator<?>>> result = toList(resolution.resolveFrom(
                new SimpleSyntheticDependencies(), repo));

        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getName(), equalTo("intGen"));
        assertThat(result.get(0).getType(), equalTo((Type) int.class));
        assertThat(result.get(1).getName(), equalTo("stringGen"));
        assertThat(result.get(1).getType(), equalTo((Type) String.class));
    }

    private static <T> List<T> toList(Iterable<T> it) {
        List<T> result = new ArrayList<T>();
        for (T t : it) {
            result.add(t);
        }
        return result;
    }
}
