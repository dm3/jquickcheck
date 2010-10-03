package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import lt.dm3.jquickcheck.api.DiscardedValue;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyInvocation.Result;
import lt.dm3.jquickcheck.api.PropertyMethod;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;
import lt.dm3.jquickcheck.sample.Sample;
import lt.dm3.jquickcheck.sample.SampleGenerator;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

@SuppressWarnings("unchecked")
public class DefaultPropertyMethodTest {

    // method accessed inside of the test
    public boolean methodA(Sample a, Integer b) {
        return true;
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void shouldCreateAnInvocationContainingAGeneratorForEachParameterInCorrectOrder() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        Generator<Sample> generator = new SampleGenerator();
        Generator<Integer> generatorInt = new IntegerGenerator();
        given(repo.has(Sample.class)).willReturn(true);
        given(repo.get(Sample.class)).willReturn((Generator) generator);
        given(repo.has(Integer.class)).willReturn(true);
        given(repo.get(Integer.class)).willReturn((Generator) generatorInt);

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

    @Test(expected = RuntimeException.class)
    public void shouldThrowAnExceptionIfImpossibleToInvokeTheReturnedInvocation() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        given(repo.has(Sample.class)).willReturn(false);
        given(repo.has(Integer.class)).willReturn(false);

        PropertyMethod<Generator<?>> method = defaultMethod("methodB");
        PropertyInvocation<Generator<?>> invocation = method.createInvocationWith(repo);

        invocation.invoke();
    }

    public boolean methodThrowsAssertionError() {
        throw new AssertionError();
    }

    @Test
    public void shouldReturnAnInvocationWhichReturnsFalseIfPropertyMethodThrowsAnAssertionError() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        PropertyMethod<Generator<?>> method = defaultMethod("methodThrowsAssertionError");
        PropertyInvocation<Generator<?>> invocation = method.createInvocationWith(repo);

        PropertyInvocation.Result result = invocation.invoke();

        assertThat(result, is(Result.FALSIFIED));
    }

    public boolean methodReturnsTrue() {
        return true;
    }

    @Test
    public void shouldReturnAnInvocationWhichReturnsProvenIfPropertyMethodReturnsTrue() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        PropertyMethod<Generator<?>> method = defaultMethod("methodReturnsTrue");
        PropertyInvocation<Generator<?>> invocation = method.createInvocationWith(repo);

        PropertyInvocation.Result result = invocation.invoke();

        assertThat(result, is(Result.PROVEN));
    }

    public boolean methodReturnsFalse() {
        return false;
    }

    @Test
    public void shouldReturnAnInvocationWhichReturnsProvenIfPropertyMethodReturnsFalse() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        PropertyMethod<Generator<?>> method = defaultMethod("methodReturnsFalse");
        PropertyInvocation<Generator<?>> invocation = method.createInvocationWith(repo);

        PropertyInvocation.Result result = invocation.invoke();

        assertThat(result, is(Result.FALSIFIED));
    }

    public int methodReturnsInt() {
        return 1;
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowAnExceptionIfMethodReturnsNotABooleanOrVoid() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        PropertyMethod<Generator<?>> method = defaultMethod("methodReturnsInt");
        PropertyInvocation<Generator<?>> invocation = method.createInvocationWith(repo);

        invocation.invoke();
    }

    public void methodReturnsVoidAndThrowsAssertionError() {
        throw new AssertionError();
    }

    @Test
    public void shouldReturnAnInvocationWhichReturnsFalseIfPropertyMethodThrowsAnAssertionErrorAndReturnsVoid() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        PropertyMethod<Generator<?>> method = defaultMethod("methodReturnsVoidAndThrowsAssertionError");
        PropertyInvocation<Generator<?>> invocation = method.createInvocationWith(repo);

        PropertyInvocation.Result result = invocation.invoke();

        assertThat(result, is(Result.FALSIFIED));
    }

    public void methodReturnsVoid() {}

    @Test
    public void shouldReturnAnInvocationWhichReturnsTrueIfPropertyMethodReturnsVoid() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        PropertyMethod<Generator<?>> method = defaultMethod("methodReturnsVoid");
        PropertyInvocation<Generator<?>> invocation = method.createInvocationWith(repo);

        Result result = invocation.invoke();

        assertThat(result, is(Result.PROVEN));
    }

    public void methodThrowsDiscarded() {
        throw new DiscardedValue();
    }

    @Test
    public void shouldReturnAnInvocationWhichIsExhaustedIfPropertyMethodThrowsDiscardedException() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        PropertyMethod<Generator<?>> method = defaultMethod("methodThrowsDiscarded");
        PropertyInvocation<Generator<?>> invocation = method.createInvocationWith(repo);

        Result result = invocation.invoke();

        assertThat(result, is(Result.DISCARDED));
    }

    public void methodWithSyntheticType(List<Integer> param) {}

    /**
     * Hacky test. Too much mocking.
     */
    @SuppressWarnings({ "rawtypes" })
    @Test
    public void shoulCallSyntheticGeneratorForAParameterizedType() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        given(repo.has(Integer.class)).willReturn(true);
        given(repo.get(Integer.class)).willReturn((Generator) new SampleGenerator());
        given(repo.has(List.class)).willReturn(false);
        given(repo.get(List.class)).willThrow(new IllegalArgumentException());
        given(repo.hasDefault(List.class)).willReturn(false);
        given(repo.getDefault(List.class)).willThrow(new IllegalArgumentException());

        try {
            PropertyMethod<Generator<?>> method = defaultMethod("methodWithSyntheticType", List.class);
            method.createInvocationWith(repo);
        } catch (QuickCheckException expected) {
            // fails because #getSyntheticGeneratorFor returns null
        }

        verify(repo).getSynthetic(eq(new TypeToken<List<Integer>>() {}.getType()));
    }

    private PropertyMethod<Generator<?>> defaultMethod(String name, Class<?>... parameters) {
        try {
            return new DefaultPropertyMethod<Generator<?>>(this.getClass().getMethod(name, parameters), this,
                                                           new DefaultInvocationSettings(1, false, true));
        } catch (NoSuchMethodException e) {
            Assert.fail(e.getMessage());
        }
        return null; // unreachable
    }
}
