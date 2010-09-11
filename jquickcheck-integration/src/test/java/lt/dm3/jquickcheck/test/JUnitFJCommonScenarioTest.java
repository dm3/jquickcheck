package lt.dm3.jquickcheck.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.fj.DefaultGenerators;
import lt.dm3.jquickcheck.fj.FJ;
import lt.dm3.jquickcheck.junit4.JUnitTestClassBuilder;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.PositiveIntGenerator;
import lt.dm3.jquickcheck.sample.SampleProvider;
import lt.dm3.jquickcheck.test.builder.AbstractTestClassBuilder;
import lt.dm3.jquickcheck.test.builder.GeneratedTest;
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

public class JUnitFJCommonScenarioTest extends AbstractCommonScenarioTest<Arbitrary> {

    @Override
    protected TestClassBuilderFactory<Arbitrary> defaultClassBuilderFactory() {
        return new TestClassBuilderFactory<Arbitrary>() {
            @Override
            public AbstractTestClassBuilder<Arbitrary> createBuilder(String className,
                                                                     Class<? super Arbitrary> generatorClass) {
                return new JUnitTestClassBuilder(className, generatorClass) {
                    @Override
                    protected Class<?> getQuickCheckProviderClass() {
                        return FJ.class;
                    }
                };
            }
        };
    }

    @Override
    protected Class<Arbitrary> generatorClass() {
        return Arbitrary.class;
    }

    @Override
    protected Iterable<GeneratorInfo> supportedGenerators() {
        return new DefaultGenerators();
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class)
    public static class PrimitiveTest {
        @Property
        public boolean shouldRunTestWithPrimitiveIntArgument(int arg) {
            return true;
        }

        @Property
        public boolean shouldRunTestWithTwoPrimitiveIntArguments(int arg, int arg2) {
            return true;
        }

        @Property
        public boolean shouldRunTestWithThreePrimitiveIntArguments(int arg, int arg2, int arg3) {
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class)
    public static class CustomParameterGeneratorInstanceTest {
        private static final Generator<Integer> positiveIntGen = new PositiveIntGenerator();

        @Property
        public boolean shouldRunTestWithCustomPrimitiveIntGenerator(@G(gen = "positiveIntGen") int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class)
    public static class OneCustomParameterGeneratorInstanceTest {
        private static final Generator<Integer> positiveIntGen = new PositiveIntGenerator();

        @Property
        public boolean shouldRunTestWithCustomPrimitiveIntGenerator(int arg) {
            return arg > 0;
        }
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
    public void shouldCheckPropertyWithNoArguments() {
        Class<?> clazz = withOneNoArgPropertyReturningTrue().load();

        doTestSuccessfulRun(clazz);
    }

    @Test
    public void shouldCheckPropertyWithNoArgumentsReturningFalse() {
        Class<?> clazz = withOneNoArgPropertyReturningFalse().load();

        Result result = JUnitCore.runClasses(clazz);

        assertThat(result.getRunCount(), equalTo(1));
        assertThat(result.getFailureCount(), equalTo(1));
        assertThat(result.getFailures().get(0).getException(), instanceOf(QuickCheckException.class));
    }

    @Test
    public void shouldResolveFieldsWithAnyCombinationOfModifiers() {
        for (GeneratedTest t : withOneFieldAndOnePropertyAndDifferentFieldModifiers()) {
            Class<?> clazz = t.load();

            doTestSuccessfulRun(clazz);
        }
    }

    @Test
    public void runPrimitiveTest() throws InitializationError {
        doTestSuccessfulRun(PrimitiveTest.class);
    }

    @Test
    public void runCustomParameterGeneratorInstanceTest() throws InitializationError {
        doTestSuccessfulRun(CustomParameterGeneratorInstanceTest.class);
    }

    @Test
    public void runOneCustomParameterGeneratorInstanceTest() throws InitializationError {
        doTestSuccessfulRun(OneCustomParameterGeneratorInstanceTest.class);
    }

    @Test
    public void runCustomPrivateFieldGeneratorTestIfInitializedInConstructor() throws InitializationError {
        doTestSuccessfulRun(CustomPrivateFieldGeneratorTest.class);
    }

    @Test
    public void runCustomPrivateFieldGeneratorTestIfInitializedInBeforeClass() throws InitializationError {
        doTestSuccessfulRun(CustomPrivateFieldSetInBeforeClassGeneratorTest.class);
    }

    /**
     * Setting generators in @Before doesn't work.
     * 
     * @throws InitializationError
     */
    @Test
    public void runCustomPrivateFieldGeneratorTestIfInitializedInBeforeShouldFail() throws InitializationError {
        Result result = JUnitCore.runClasses(CustomPrivateFieldSetInBeforeGeneratorTest.class);
        int totalTests = new TestClass(CustomPrivateFieldSetInBeforeGeneratorTest.class)
                .getAnnotatedMethods(Property.class).size();

        assertThat(result.getFailureCount(), equalTo(totalTests));
        assertThat(result.getRunCount(), equalTo(totalTests));
    }

    private static void doTestSuccessfulRun(Class<?> testClass) {
        Result result = JUnitCore.runClasses(testClass);
        int totalTests = new TestClass(testClass).getAnnotatedMethods(Property.class).size();

        assertThat(result.getFailureCount(), equalTo(0));
        assertThat(result.getRunCount(), equalTo(totalTests));
    }

}
