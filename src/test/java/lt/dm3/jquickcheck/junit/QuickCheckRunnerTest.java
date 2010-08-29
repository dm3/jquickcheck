package lt.dm3.jquickcheck.junit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
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
        @Test
        public boolean shouldRunTheTestWithNoArguments() {
            return true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    public static class PrimitiveTest {
        @Test
        public boolean shouldRunTheTestWithPrimitiveIntArgument(int arg) {
            return true;
        }

        @Test
        public boolean shouldRunTheTestWithPrimitiveDoubleArgument(double arg) {
            return true;
        }

        @Test
        public boolean shouldRunTheTestWithPrimitiveLongArgument(long arg) {
            return true;
        }

        @Test
        public boolean shouldRunTheTestWithPrimitiveShortArgument(short arg) {
            return true;
        }
    }

    @Test
    public void runActualTest() throws InitializationError {
        Result result = JUnitCore.runClasses(ActualTest.class);
        int totalTests = new TestClass(ActualTest.class).getAnnotatedMethods(Test.class).size();

        assertThat(result.getFailureCount(), equalTo(0));
        assertThat(result.getRunCount(), equalTo(totalTests));
    }

    @Test
    public void runPrimitiveTest() throws InitializationError {
        Result result = JUnitCore.runClasses(PrimitiveTest.class);
        int totalTests = new TestClass(PrimitiveTest.class).getAnnotatedMethods(Test.class).size();

        assertThat(result.getFailureCount(), equalTo(0));
        assertThat(result.getRunCount(), equalTo(totalTests));
    }
}
