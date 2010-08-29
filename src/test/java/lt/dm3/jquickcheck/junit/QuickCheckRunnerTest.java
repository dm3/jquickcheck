package lt.dm3.jquickcheck.junit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import lt.dm3.jquickcheck.junit.runners.QuickCheckRunner;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

public class QuickCheckRunnerTest {

    private static AtomicInteger count = new AtomicInteger(0);

    @RunWith(QuickCheckRunner.class)
    public static class SimpleTest {
        @Test
        public void shouldRunTheTestWithNoArguments() throws InitializationError {
            count.incrementAndGet();
        }

    }

    @Test
    public void runSimpleTest() throws InitializationError {
        Result result = JUnitCore.runClasses(SimpleTest.class);

        assertThat(result.getFailureCount(), equalTo(0));
        assertThat(result.getRunCount(), equalTo(1));
        // we need to check if the test method body was actually run by our runner
        assertThat(count.get(), equalTo(1));
    }
}
