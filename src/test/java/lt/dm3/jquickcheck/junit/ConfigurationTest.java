package lt.dm3.jquickcheck.junit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.fj.FJ;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.model.TestClass;

public class ConfigurationTest {

    private final int DEFAULT_SUCCESSFUL_RUNS;

    public ConfigurationTest() throws SecurityException, NoSuchMethodException {
        DEFAULT_SUCCESSFUL_RUNS = (Integer) QuickCheck.class.getMethod("minSuccessful").getDefaultValue();
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = FJ.class)
    public static class DefaultClassLevelConfigurationTest {
        private static int counter = 0;

        @Property
        public boolean shouldBeRunNTimes(int arg) {
            counter++;
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = FJ.class, minSuccessful = 50)
    public static class CustomClassLevelConfigurationTest {
        private static int counter = 0;

        @Property
        public boolean shouldBeRunNTimes(int arg) {
            counter++;
            return true;
        }
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

    private static void doTest(Class<?> testClass) {
        Result result = JUnitCore.runClasses(testClass);
        int totalTests = new TestClass(testClass).getAnnotatedMethods(Property.class).size();

        assertThat(result.getFailureCount(), equalTo(0));
        assertThat(result.getRunCount(), equalTo(totalTests));
    }
}
