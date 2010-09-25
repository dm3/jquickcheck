package lt.dm3.jquickcheck.api.impl;

import static lt.dm3.hamcrest.matchers.AllMatchers.sameTypeAs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.GeneratorTypeResolver;
import lt.dm3.jquickcheck.api.impl.resolution.GeneratorResolutionStrategyTest;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.Sample;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Assert;
import org.junit.Test;

public class TypeFromFieldResolverTest {

    private final GeneratorTypeResolver<Field> resolver = new TypeFromFieldResolver();

    public static class FieldWithTypeParameter extends GeneratorResolutionStrategyTest.FieldTest {
        public Generator<Sample> gen = new SampleGenerator();
    }

    public static class FieldWithNoTypeParameters extends GeneratorResolutionStrategyTest.FieldTest {
        @SuppressWarnings("rawtypes")
        public Generator gen = new SampleGenerator();
    }

    public static class Pair<A, B> { }

    public static class FieldWithTwoTypeParameters extends GeneratorResolutionStrategyTest.FieldTest {
        public Pair<String, Integer> gen = new Pair<String, Integer>() { };
    }

    @Test
    public void shouldResolveTypeFromFieldTypeParameter() {
        Type result = resolver.resolveFrom(getGeneratorField(new FieldWithTypeParameter()));

        assertThat(result, sameTypeAs(Sample.class));
    }

    @Test
    public void shouldReturnNullIfSeveralFieldTypeParametersFound() {
        Type result = resolver.resolveFrom(getGeneratorField(new FieldWithTwoTypeParameters()));

        assertThat(result, is(nullValue()));
    }

    @Test
    public void shouldReturnNullIfNoFieldTypeParameterFound() {
        Type result = resolver.resolveFrom(getGeneratorField(new FieldWithNoTypeParameters()));

        assertThat(result, is(nullValue()));
    }

    private static Field getGeneratorField(Object instance) {
        try {
            return instance.getClass().getField("gen");
        } catch (SecurityException e) {
            Assert.fail(e.getMessage());
        } catch (NoSuchFieldException e) {
            Assert.fail(e.getMessage());
        }
        return null; // unreachable
    }
}
