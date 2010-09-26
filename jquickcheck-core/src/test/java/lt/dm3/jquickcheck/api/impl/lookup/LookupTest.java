package lt.dm3.jquickcheck.api.impl.lookup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;

import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.impl.lookup.DefaultLookupByName;
import lt.dm3.jquickcheck.api.impl.lookup.DefaultLookupByType;
import lt.dm3.jquickcheck.api.impl.resolution.NamedGenerator;
import lt.dm3.jquickcheck.api.impl.resolution.TypedGenerator;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.GeneratorHolder;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

public class LookupTest {

    private Lookup<String, Generator<?>> byName;
    private Lookup<Type, Generator<?>> byType;

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoGeneratorOfTheSpecifiedTypeExists() {
        byType = DefaultLookupByType.from(Collections.<TypedGenerator<Generator<?>>> emptyList());

        assertThat(byType.has(this.getClass()), is(false));
        byType.get(this.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoGeneratorOfTheSpecifiedNameExists() {
        byName = DefaultLookupByName.from(Collections.<NamedGenerator<Generator<?>>> emptyList());

        assertThat(byName.has("name"), is(false));
        byName.get("name");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void shouldGetGeneratorForType() {
        Class<Integer> type = int.class;
        SampleGenerator generator = new SampleGenerator();
        TypedGenerator<Generator<?>> holder = new GeneratorHolder(type, "a", generator);
        byType = DefaultLookupByType.from(Arrays.asList(holder));

        assertThat(byType.has(type), is(true));
        assertThat(byType.get(type), is((Generator) generator));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void shouldGetGeneratorForName() {
        String name = "a";
        SampleGenerator generator = new SampleGenerator();
        NamedGenerator<Generator<?>> holder = new GeneratorHolder(int.class, name, generator);
        byName = DefaultLookupByName.from(Arrays.asList(holder));

        assertThat(byName.has(name), is(true));
        assertThat(byName.get(name), is((Generator) generator));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnRequestByNameIfSeveralGeneratorsExistForTheSameName() {
        String name = "a";
        // must be different instances of a generator
        GeneratorHolder holder = new GeneratorHolder(int.class, name, new SampleGenerator());
        GeneratorHolder holder2 = new GeneratorHolder(double.class, name, new SampleGenerator());

        byName = DefaultLookupByName.from(Arrays.asList((NamedGenerator<Generator<?>>) holder, holder2));

        byName.get(name);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnRequestByTypeIfSeveralGeneratorsExistForTheSameType() {
        // must be different instances of a generator
        Class<Integer> type = int.class;
        GeneratorHolder holder = new GeneratorHolder(type, "a", new SampleGenerator());
        GeneratorHolder holder2 = new GeneratorHolder(type, "b", new SampleGenerator());

        byType = DefaultLookupByType.from(Arrays.asList((TypedGenerator<Generator<?>>) holder, holder2));

        byType.get(type);
    }
}
