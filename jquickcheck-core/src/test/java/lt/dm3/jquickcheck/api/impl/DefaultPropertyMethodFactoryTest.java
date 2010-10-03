package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyInvocation.Result;
import lt.dm3.jquickcheck.api.PropertyMethod;
import lt.dm3.jquickcheck.api.PropertyMethodFactory;
import lt.dm3.jquickcheck.internal.Annotations;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class DefaultPropertyMethodFactoryTest {

    private final PropertyMethodFactory<Generator<?>> factory = new DefaultPropertyMethodFactory<Generator<?>>(
                                                     new DefaultInvocationSettings(
                                                             Annotations.newInstance(QuickCheck.class)));

    public boolean noArgsNoAnnotation() {
        return true;
    }

    @Test
    public void shouldCreatePropertyMethodFromANoArgumentsMethod() throws SecurityException, NoSuchMethodException {
        PropertyMethod<Generator<?>> m = factory.createMethod(this.getClass().getMethod("noArgsNoAnnotation"), this);

        PropertyInvocation<Generator<?>> i = m.createInvocationWith(mock(GeneratorRepository.class));

        assertThat(i.invoke(), is(Result.PROVEN));
        assertThat(i.generators().isEmpty(), is(true));
        assertThat(i.settings().minSuccessful(), equalTo(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL));
    }

    @Property(minSuccessful = 50)
    public boolean noArgsWithAnnotation() {
        return true;
    }

    @Test
    public void shouldCreatePropertyMethodFromANoArgumentsMethodWithAnnotation() throws SecurityException,
        NoSuchMethodException {
        PropertyMethod<Generator<?>> m = factory.createMethod(this.getClass().getMethod("noArgsWithAnnotation"), this);

        PropertyInvocation<Generator<?>> i = m.createInvocationWith(mock(GeneratorRepository.class));

        assertThat(i.invoke(), is(Result.PROVEN));
        assertThat(i.generators().isEmpty(), is(true));
        assertThat(i.settings().minSuccessful(), equalTo(50));
    }

    public boolean someArgs(int arg) {
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldCreatePropertyMethodFromAMethodWithArguments() throws SecurityException, NoSuchMethodException {
        PropertyMethod<Generator<?>> m = factory.createMethod(this.getClass().getMethod("someArgs", int.class), this);
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        given(repo.has(int.class)).willReturn(true);
        given(repo.get(int.class)).willReturn((Generator) new IntegerGenerator());

        PropertyInvocation<Generator<?>> i = m.createInvocationWith(repo);

        assertThat(i.invoke(1), is(Result.PROVEN));
        assertThat(i.generators().size(), equalTo(1));
        assertThat(i.settings().minSuccessful(), equalTo(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL));
    }

    public void noArgsVoid() {}

    @Test
    public void shouldCreatePropertyMethodFromAMethodWithVoidReturnType() throws SecurityException,
        NoSuchMethodException {
        PropertyMethod<Generator<?>> m = factory.createMethod(this.getClass().getMethod("noArgsVoid"), this);

        PropertyInvocation<Generator<?>> i = m.createInvocationWith(mock(GeneratorRepository.class));

        assertThat(i.invoke(), is(Result.PROVEN));
        assertThat(i.generators().size(), equalTo(0));
    }

    public Integer returnsInteger() {
        return 1;
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNonBooleanReturnIsEncountered() throws SecurityException, NoSuchMethodException {
        PropertyMethod<Generator<?>> m = factory.createMethod(this.getClass().getMethod("returnsInteger"), this);

        PropertyInvocation<Generator<?>> i = m.createInvocationWith(mock(GeneratorRepository.class));

        i.invoke();
    }
}
