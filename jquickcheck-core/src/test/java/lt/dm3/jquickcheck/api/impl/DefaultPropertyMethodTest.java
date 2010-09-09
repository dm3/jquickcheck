package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyMethod;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;
import lt.dm3.jquickcheck.sample.Sample;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Assert;
import org.junit.Test;

public class DefaultPropertyMethodTest {

    // method accessed inside of the test
    public boolean methodA(Sample a, Integer b) {
        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldCreateAnInvocationContainingAGeneratorForEachParameterInCorrectOrder() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        Generator<Sample> generator = new SampleGenerator();
        Generator<Integer> generatorInt = new IntegerGenerator();
        given(repo.hasGeneratorFor(Sample.class)).willReturn(true);
        given(repo.getGeneratorFor(Sample.class)).willReturn((Generator) generator);
        given(repo.hasGeneratorFor(Integer.class)).willReturn(true);
        given(repo.getGeneratorFor(Integer.class)).willReturn((Generator) generatorInt);

        PropertyMethod<Generator<?>> method = defaultMethod("methodA", Sample.class, Integer.class);
        PropertyInvocation<Generator<?>> invocation = method.createInvocationWith(repo);

        assertThat(invocation.generators().size(), equalTo(2));
        assertThat(invocation.generators().get(0), is((Generator) generator));
        assertThat(invocation.generators().get(1), is((Generator) generatorInt));
    }

    // method accessed inside of the test
    public boolean methodB() throws InvocationTargetException {
        throw new InvocationTargetException(new RuntimeException("lol"));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = RuntimeException.class)
    public void shouldThrowAnExceptionIfImpossibleToInvokeTheReturnedInvocation() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        given(repo.hasGeneratorFor(Sample.class)).willReturn(false);
        given(repo.hasGeneratorFor(Integer.class)).willReturn(false);

        PropertyMethod<Generator<?>> method = defaultMethod("methodB");
        PropertyInvocation<Generator<?>> invocation = method.createInvocationWith(repo);

        invocation.invoke();
    }

    private PropertyMethod<Generator<?>> defaultMethod(String name, Class<?>... parameters) {
        try {
            return new DefaultPropertyMethod<Generator<?>>(this.getClass().getMethod(name, parameters), this,
                                                           new DefaultInvocationSettings(1));
        } catch (NoSuchMethodException e) {
            Assert.fail(e.getMessage());
        }
        return null; // unreachable
    }
}
