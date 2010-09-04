package lt.dm3.jquickcheck.junit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.fj.FJGeneratorResolutionStrategy;
import lt.dm3.jquickcheck.junit4.Generator;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class QuickCheckRunnerTest {

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(resolutionStrategy = FJGeneratorResolutionStrategy.class)
    public static class ActualTest {
        @Property
        public boolean shouldRunTheTestWithNoArguments() {
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(resolutionStrategy = FJGeneratorResolutionStrategy.class)
    public static class PrimitiveTest {
        @Property
        public boolean shouldRunTheTestWithPrimitiveIntArgument(int arg) {
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(resolutionStrategy = FJGeneratorResolutionStrategy.class)
    public static class CustomParameterGeneratorClassTest {
        @Property
        public boolean shouldRunTestWithCustomPrimitiveIntGenerator(@G(genClass = PositiveIntGen.class) int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(resolutionStrategy = FJGeneratorResolutionStrategy.class)
    public static class CustomParameterGeneratorInstanceTest {
        @G
        private static final Generator<Integer> positiveIntGen = new PositiveIntGen();

        @Property
        public boolean shouldRunTestWithCustomPrimitiveIntGenerator(@G(gen = "positiveIntGen") int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(resolutionStrategy = FJGeneratorResolutionStrategy.class)
    public static class OneCustomParameterGeneratorInstanceTest {
        private static final Generator<Integer> positiveIntGen = new PositiveIntGen();

        @Property
        public boolean shouldRunTestWithCustomPrimitiveIntGenerator(int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(resolutionStrategy = FJGeneratorResolutionStrategy.class)
    public static class CustomPrivateFinalFieldGeneratorTest {
        @G
        private final Generator<Integer> positiveIntGen = new PositiveIntGen();

        @Property
        public boolean shouldRunTestWithDefaultGeneratorSpecifiedEarlier(int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(resolutionStrategy = FJGeneratorResolutionStrategy.class)
    public static class CustomPrivateFinalStaticFieldGeneratorTest {
        @G
        private final static Generator<Integer> positiveIntGen = new PositiveIntGen();

        @Property
        public boolean shouldRunTestWithDefaultGeneratorSpecifiedEarlier(int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(resolutionStrategy = FJGeneratorResolutionStrategy.class)
    public static class CustomPrivateFieldGeneratorTest {
        @G
        private final Generator<Integer> positiveIntGen;

        public CustomPrivateFieldGeneratorTest() {
            positiveIntGen = new PositiveIntGen();
        }

        @Property
        public boolean shouldRunTestWithDefaultGeneratorSpecifiedEarlier(int arg) {
            return arg > 0;
        }
    }

    @Test
    public void runActualTest() throws InitializationError {
        doTest(ActualTest.class);
    }

    @Test
    public void runPrimitiveTest() throws InitializationError {
        doTest(PrimitiveTest.class);
    }

    @Test
    public void runCustomParameterGeneratorClassTest() throws InitializationError {
        doTest(CustomParameterGeneratorClassTest.class);
    }

    @Test
    public void runCustomParameterGeneratorInstanceTest() throws InitializationError {
        doTest(CustomParameterGeneratorInstanceTest.class);
    }

    @Test
    public void runOneCustomParameterGeneratorInstanceTest() throws InitializationError {
        doTest(OneCustomParameterGeneratorInstanceTest.class);
    }

    @Test
    public void runCustomPrivateFinalFieldGeneratorTest() throws InitializationError {
        doTest(CustomPrivateFinalFieldGeneratorTest.class);
    }

    @Test
    public void runCustomPrivateFinalStaticFieldGeneratorTest() throws InitializationError {
        doTest(CustomPrivateFinalStaticFieldGeneratorTest.class);
    }

    @Test
    public void runCustomPrivateFieldGeneratorTestIfInitializedInConstructor() throws InitializationError {
        doTest(CustomPrivateFieldGeneratorTest.class);
    }

    private static void doTest(Class<?> testClass) {
        Result result = JUnitCore.runClasses(testClass);
        int totalTests = new TestClass(testClass).getAnnotatedMethods(Property.class).size();

        assertThat(result.getFailureCount(), equalTo(0));
        assertThat(result.getRunCount(), equalTo(totalTests));
    }

}
