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

public class TypeFromInstanceResolverTest {

    private final GeneratorTypeResolver<Generator<?>> resolver = new TypeFromInstanceResolver<Generator<?>>();

    public static class FieldTestWithGenField extends TestData.FieldTest {
        public Generator<Sample> gen;

    }

    public static class GeneratorWithTypeParameterInTheSuperInterface extends FieldTestWithGenField {
        {
            gen = new SampleGenerator();
        }
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

    @SuppressWarnings("unchecked")
    public static class GeneratorWithTypeParameterInTheSuperclass extends FieldTestWithGenField {
        {
            gen = new GeneratorWithSuperHavingType();
        }
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

    public static class GeneratorWithTypeParameterInTheInterfaceOfSuperclass extends FieldTestWithGenField {
        {
            gen = new GeneratorWithSuperHavingNoType();
        }
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
        Type result = resolver.resolveFrom(new GeneratorWithTypeParameterInTheSuperclass().gen);

        assertThat(result, sameTypeAs(Sample.class));
    }

    @Test
    public void shouldResolveTypeFromSuperclassInterfaceTypeParameter() {
        Type result = resolver.resolveFrom(new GeneratorWithTypeParameterInTheInterfaceOfSuperclass().gen);

        assertThat(result, sameTypeAs(Sample.class));
    }

    @Test
    public void shouldResolveTypeFromSuperInterfaceTypeParameter() {
        Type result = resolver.resolveFrom(new GeneratorWithTypeParameterInTheSuperInterface().gen);

        assertThat(result, sameTypeAs(Sample.class));
    }

    @Test
    public void shouldResolveTypeFromAnonymousClassWithTypeParameter() {
        Type result = resolver.resolveFrom(new AnonymousGeneratorWithTypeParameter().gen);

        assertThat(result, sameTypeAs(Sample.class));
    }

    @Test
    public void shouldReturnNullIfGeneratorHasNoTypeParameters() {
        Type result = resolver.resolveFrom(new GeneratorWithNoTypeParameters().gen);

        assertThat(result, is(nullValue()));
    }

}
