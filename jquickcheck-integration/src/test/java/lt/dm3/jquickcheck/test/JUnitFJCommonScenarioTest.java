package lt.dm3.jquickcheck.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.fj.DefaultGenerators;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.PositiveIntGenerator;
import lt.dm3.jquickcheck.sample.SampleProvider;
import lt.dm3.jquickcheck.test.builder.GeneratorInfo;
import lt.dm3.jquickcheck.test.builder.TestClassBuilderFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import fj.test.Arbitrary;
import fj.test.Gen;

public class JUnitFJCommonScenarioTest extends AbstractCommonScenarioTest<Arbitrary<?>> {

    @Override
    protected TestClassBuilderFactory<Arbitrary<?>> defaultClassBuilderFactory() {
        return new JUnitFJFactory();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<Arbitrary<?>> generatorClass() {
        return (Class) Arbitrary.class;
    }

    @Override
    protected Iterable<GeneratorInfo> supportedGenerators() {
        return new DefaultGenerators();
    }

    public static final Arbitrary<Integer> arbPositiveInt = Arbitrary.arbitrary(Gen.choose(1, 100));

    @Override
    protected GeneratorInfo positiveIntegerGenerator() {
        return new GeneratorInfo(this.getClass().getName() + ".arbPositiveInt;", Integer.class);
    }

    @Override
    protected GeneratorInfo anyIntegerGenerator() {
        return new GeneratorInfo(Arbitrary.class.getName() + ".arbInteger;", Integer.class);
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class)
    public static class CustomPrivateFieldGeneratorTest {
        private final Generator<Integer> positiveIntGen;

        public CustomPrivateFieldGeneratorTest() {
            positiveIntGen = new PositiveIntGenerator();
        }

        @Property
        public boolean shouldRunTestWithDefaultGeneratorSpecifiedEarlier(int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class)
    public static class CustomPrivateFieldSetInBeforeClassGeneratorTest {
        private static Generator<Integer> positiveIntGen;

        @BeforeClass
        public static void beforeClass() {
            positiveIntGen = new PositiveIntGenerator();
        }

        @Property
        public boolean shouldRunTestWithDefaultGeneratorSpecifiedEarlier(int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class)
    public static class CustomPrivateFieldSetInBeforeGeneratorTest {
        private Generator<Integer> positiveIntGen;

        @Before
        public void before() {
            // this is effectively a noop
            positiveIntGen = new PositiveIntGenerator();
        }

        @Property
        public boolean shouldRunTestWithDefaultGeneratorSpecifiedEarlier(int arg) {
            return arg > 0;
        }
    }

    @Test
    public void runCustomPrivateFieldGeneratorTestIfInitializedInConstructor() throws InitializationError {
        checkNoPropertiesFailIn(CustomPrivateFieldGeneratorTest.class);
    }

    @Test
    public void runCustomPrivateFieldGeneratorTestIfInitializedInBeforeClass() throws InitializationError {
        checkNoPropertiesFailIn(CustomPrivateFieldSetInBeforeClassGeneratorTest.class);
    }

    /**
     * Setting generators in @Before doesn't work.
     * 
     * @throws InitializationError
     */
    @Test
    public void runCustomPrivateFieldGeneratorTestIfInitializedInBeforeShouldFail() throws InitializationError {
        checkAllPropertiesFailIn(CustomPrivateFieldSetInBeforeGeneratorTest.class);
    }

    @Override
    protected void checkNoPropertiesFailIn(Class<?> clazz) {
        Result result = JUnitCore.runClasses(clazz);
        int totalTests = new TestClass(clazz).getAnnotatedMethods(Property.class).size();

        assertThat(result.getFailureCount(), equalTo(0));
        assertThat(result.getRunCount(), equalTo(totalTests));
    }

    @Override
    protected void checkAllPropertiesFailIn(Class<?> clazz) {
        Result result = JUnitCore.runClasses(clazz);
        int totalTests = new TestClass(clazz).getAnnotatedMethods(Property.class).size();

        assertThat(result.getRunCount(), equalTo(totalTests));
        assertThat(result.getFailureCount(), equalTo(totalTests));
        assertThat(result.getFailures().get(0).getException(), instanceOf(QuickCheckException.class));
    }

}
