package lt.dm3.jquickcheck.junit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.impl.DefaultInvocationSettings;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;
import lt.dm3.jquickcheck.sample.SampleProvider;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.model.TestClass;

public class ConfigurationTest {

    private final int DEFAULT_SUCCESSFUL_RUNS = DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL;

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class)
    public static class DefaultClassLevelConfigurationTestFailing {
        @Property
        public boolean shouldFailAsNotUsingDefaults(int arg) {
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class, useDefaults = true)
    public static class DefaultClassLevelConfigurationTest {
        private static int counter = 0;

        @Property
        public boolean shouldBeRunNTimes(int arg) {
            counter++;
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class, minSuccessful = 50, useDefaults = true)
    public static class CustomClassLevelConfigurationTest {
        private static int counter = 0;

        @Property
        public boolean shouldBeRunNTimes(int arg) {
            counter++;
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class, useDefaults = true)
    public static class CustomPropertyLevelConfigurationTest {
        private static int counter = 0;

        @Property
        public boolean shouldBeRunDefaultTimes(int arg) {
            counter++;
            return true;
        }

        @Property(minSuccessful = 50)
        public boolean shouldBeRunNTimes(int arg) {
            counter++;
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = SampleProvider.class, minSuccessful = 10, useDefaults = true)
    public static class CustomPropertyLevelPriorityConfigurationTest {
        private static int counter = 0;

        @Property
        public boolean shouldBeRunDefaultTimes(int arg) {
            counter++;
            return true;
        }

        @Property(minSuccessful = 50)
        public boolean shouldBeRunNTimes(int arg) {
            counter++;
            return true;
        }
    }

    @Test
    public void shouldFailWhenUseDefaultsSetToFalseOnClassLevel() {
        Result result = JUnitCore.runClasses(DefaultClassLevelConfigurationTestFailing.class);
        int totalTests = new TestClass(DefaultClassLevelConfigurationTestFailing.class)
                .getAnnotatedMethods(Property.class).size();

        assertThat(result.getFailureCount(), equalTo(totalTests));
        assertThat(result.getFailures().get(0).getException(), instanceOf(QuickCheckException.class));
        assertThat(result.getRunCount(), equalTo(totalTests));
    }

    @Test
    public void shouldRunEachPropertyWithArgumentsNTimesByDefault() {
        doTest(DefaultClassLevelConfigurationTest.class);

        assertThat(DefaultClassLevelConfigurationTest.counter, equalTo(DEFAULT_SUCCESSFUL_RUNS));
    }

    @Test
    public void shouldRunEachPropertyWithArgumentsSpecifiedNumberOfTimes() {
        doTest(CustomClassLevelConfigurationTest.class);

        assertThat(CustomClassLevelConfigurationTest.counter, equalTo(50));
    }

    @Test
    public void shouldRunPropertyWithArgumentsNumberOfTimesSpecifiedAtThePropertyLevel() {
        doTest(CustomPropertyLevelConfigurationTest.class);

        assertThat(CustomPropertyLevelConfigurationTest.counter, equalTo(DEFAULT_SUCCESSFUL_RUNS + 50));
    }

    @Test
    public void shouldRunPropertyNumberOfTimesSpecifiedAtThePropertyLevelFirstAndThenLookAtTestCaseLevel() {
        doTest(CustomPropertyLevelPriorityConfigurationTest.class);

        assertThat(CustomPropertyLevelPriorityConfigurationTest.counter, equalTo(10 + 50));
    }

    private static void doTest(Class<?> testClass) {
        Result result = JUnitCore.runClasses(testClass);
        int totalTests = new TestClass(testClass).getAnnotatedMethods(Property.class).size();

        assertThat(result.getFailureCount(), equalTo(0));
        assertThat(result.getRunCount(), equalTo(totalTests));
    }
}
