package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;

import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyMethod;
import lt.dm3.jquickcheck.api.PropertyMethodFactory;
import lt.dm3.jquickcheck.internal.Annotations;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.GeneratorHolder;
import lt.dm3.jquickcheck.sample.IntegerGenerator;

import org.junit.Test;

public class DefaultPropertyMethodFactoryTest {

    static class TestRepo extends DefaultGeneratorRepository<Generator<?>> {

        public TestRepo(Iterable<? extends NamedAndTypedGenerator<Generator<?>>> generators) {
            super(generators, null);
        }

        @Override
        public Generator<?> getDefaultGeneratorFor(Type t) {
            throw new UnsupportedOperationException("I heard you liked exceptions.");
        }

    }

    private final PropertyMethodFactory<Generator<?>> factory = new DefaultPropertyMethodFactory<Generator<?>>(
                                                     new DefaultInvocationSettings(
                                                             Annotations.newInstance(QuickCheck.class)));

    public boolean noArgsNoAnnotation() {
        return true;
    }

    @Test
    public void shouldCreatePropertyMethodFromANoArgumentsMethod() throws SecurityException, NoSuchMethodException {
        PropertyMethod<Generator<?>> m = factory.createMethod(this.getClass().getMethod("noArgsNoAnnotation"), this);

        PropertyInvocation<Generator<?>> i = m.createInvocationWith(new TestRepo(Collections
                .<GeneratorHolder> emptyList()));

        assertThat(i.invoke(), is(true));
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

        PropertyInvocation<Generator<?>> i = m.createInvocationWith(new TestRepo(Collections
                .<GeneratorHolder> emptyList()));

        assertThat(i.invoke(), is(true));
        assertThat(i.generators().isEmpty(), is(true));
        assertThat(i.settings().minSuccessful(), equalTo(50));
    }

    public boolean someArgs(int arg) {
        return true;
    }

    @Test
    public void shouldCreatePropertyMethodFromAMethodWithArguments() throws SecurityException, NoSuchMethodException {
        PropertyMethod<Generator<?>> m = factory.createMethod(this.getClass().getMethod("someArgs", int.class), this);

        PropertyInvocation<Generator<?>> i = m.createInvocationWith(
                                                 new TestRepo(Arrays.asList(
                                                         new GeneratorHolder(int.class, "", new IntegerGenerator()))));

        assertThat(i.invoke(1), is(true));
        assertThat(i.generators().size(), equalTo(1));
        assertThat(i.settings().minSuccessful(), equalTo(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL));
    }

    public void noArgsVoid() {}

    @Test
    public void shouldCreatePropertyMethodFromAMethodWithVoidReturnType() throws SecurityException,
        NoSuchMethodException {
        PropertyMethod<Generator<?>> m = factory.createMethod(this.getClass().getMethod("noArgsVoid"), this);

        PropertyInvocation<Generator<?>> i = m.createInvocationWith(
                                                new TestRepo(Collections.<GeneratorHolder> emptyList()));

        assertThat(i.invoke(), is(true));
        assertThat(i.generators().size(), equalTo(0));
    }

    public Integer returnsInteger() {
        return 1;
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNonBooleanReturnIsEncountered() throws SecurityException, NoSuchMethodException {
        PropertyMethod<Generator<?>> m = factory.createMethod(this.getClass().getMethod("returnsInteger"), this);

        PropertyInvocation<Generator<?>> i = m.createInvocationWith(
                                                new TestRepo(Collections.<GeneratorHolder> emptyList()));

        i.invoke();
    }
}
