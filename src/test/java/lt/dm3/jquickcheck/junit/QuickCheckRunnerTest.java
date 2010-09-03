package lt.dm3.jquickcheck.junit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.junit.runners.Arb;
import lt.dm3.jquickcheck.junit.runners.Generator;
import lt.dm3.jquickcheck.junit.runners.QuickCheckRunner;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class QuickCheckRunnerTest {

    @RunWith(QuickCheckRunner.class)
    public static class ActualTest {
        @Property
        public boolean shouldRunTheTestWithNoArguments() {
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    public static class PrimitiveTest {
        @Property
        public boolean shouldRunTheTestWithPrimitiveIntArgument(int arg) {
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    public static class CustomParameterGeneratorClassTest {
        @Property
        public boolean shouldRunTestWithCustomPrimitiveIntGenerator(@Arb(genClass = PositiveIntGen.class) int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    public static class CustomParameterGeneratorInstanceTest {
        @Arb
        private static final Generator<Integer> positiveIntGen = new PositiveIntGen();

        @Property
        public boolean shouldRunTestWithCustomPrimitiveIntGenerator(@Arb(gen = "positiveIntGen") int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    public static class CustomPrivateFinalFieldGeneratorTest {
        @Arb
        private final Generator<Integer> positiveIntGen = new PositiveIntGen();

        @Property
        public boolean shouldRunTestWithDefaultGeneratorSpecifiedEarlier(int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    public static class CustomPrivateFinalStaticFieldGeneratorTest {
        @Arb
        private final static Generator<Integer> positiveIntGen = new PositiveIntGen();

        @Property
        public boolean shouldRunTestWithDefaultGeneratorSpecifiedEarlier(int arg) {
            return arg > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    public static class CustomPrivateFieldGeneratorTest {
        @Arb
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
