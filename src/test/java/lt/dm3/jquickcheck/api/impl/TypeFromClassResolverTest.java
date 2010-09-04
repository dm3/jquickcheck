package lt.dm3.jquickcheck.api.impl;

import static lt.dm3.hamcrest.matchers.AllMatchers.sameTypeAs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.GeneratorTypeResolver;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.Sample;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Test;

public class TypeFromClassResolverTest {

    private final GeneratorTypeResolver<Class<?>> resolver = new TypeFromClassResolver();

    public static class FieldTestWithGenField extends GeneratorResolutionStrategyTest.FieldTest {
        public Generator<Sample> gen;

    }

    public static class AnonymousGeneratorWithTypeParameter extends FieldTestWithGenField {
        {
            gen = new Generator<Sample>() {
                @Override
                public Sample generate() {
                    return new Sample();
                }
            };
        }
    }

    // super class with type + interface with no type
    @SuppressWarnings("rawtypes")
    public static class GeneratorSuper<T> implements Generator {
        @Override
        public Sample generate() {
            return new Sample();
        }
    }

    public static class GeneratorWithSuperHavingType extends GeneratorSuper<Sample> {
    }

    // super class with no type + interface with type
    public static class GeneratorSuperWithNoType implements Generator<Sample> {
        @Override
        public Sample generate() {
            return new Sample();
        }
    }

    public static class GeneratorWithSuperHavingNoType extends GeneratorSuperWithNoType {
    }

    // interface with > 1 type parameters
    private interface Pair<A, B> {
    }

    public static class GeneratorWithTwoTypeParametersInTheSuperInterface implements Pair<Integer, String> {
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static class GeneratorWithNoTypeParameters extends FieldTestWithGenField {
        {
            gen = new Generator() {
                @Override
                public Object generate() {
                    return new Sample();
                }
            };
        }
    }

    @Test
    public void shouldResolveTypeFromSuperclassTypeParameter() {
        Type result = resolver.resolveFrom(GeneratorWithSuperHavingType.class);

        assertThat(result, sameTypeAs(Sample.class));
    }

    @Test
    public void shouldResolveTypeFromSuperclassInterfaceTypeParameter() {
        Type result = resolver.resolveFrom(GeneratorWithSuperHavingNoType.class);

        assertThat(result, sameTypeAs(Sample.class));
    }

    @Test
    public void shouldResolveTypeFromSuperInterfaceTypeParameter() {
        Type result = resolver.resolveFrom(SampleGenerator.class);

        assertThat(result, sameTypeAs(Sample.class));
    }

    @Test
    public void shouldResolveTypeFromAnonymousClassWithTypeParameter() {
        Type result = resolver.resolveFrom(new AnonymousGeneratorWithTypeParameter().gen.getClass());

        assertThat(result, sameTypeAs(Sample.class));
    }

    @Test
    public void shouldReturnNullIfSuperInterfaceHasMoreThanOneTypeParameter() {
        Type result = resolver.resolveFrom(GeneratorWithTwoTypeParametersInTheSuperInterface.class);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void shouldReturnNullIfGeneratorHasNoTypeParameters() {
        Type result = resolver.resolveFrom(new GeneratorWithNoTypeParameters().gen.getClass());

        assertThat(result, is(nullValue()));
    }

}
