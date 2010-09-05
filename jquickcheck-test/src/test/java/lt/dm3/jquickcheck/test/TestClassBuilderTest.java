package lt.dm3.jquickcheck.test;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import javassist.NotFoundException;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

public class TestClassBuilderTest {

    @Test
    public void shouldCreateATestClass() throws ClassNotFoundException, NotFoundException {
        TestClassBuilder.forJUnit4().build("lol");

        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("lol");
        RunWith runAnnotation = clazz.getAnnotation(RunWith.class);
        Class<? extends Runner> runnerClass = runAnnotation.value();

        assertThat(runnerClass, typeCompatibleWith(QuickCheckRunner.class));
    }
}
