package lt.dm3.jquickcheck.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Random;

import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TestClassGeneratorTest {

    private final Generator<GeneratorInfo> gen = new Generator<GeneratorInfo>() {
        private final Random r = new Random();

        public GeneratorInfo generate() {
            if (r.nextBoolean()) {
                return new GeneratorInfo(ClassUtils.newInstance(IntegerGenerator.class), Integer.class);
            }
            return new GeneratorInfo(ClassUtils.newInstance(ListIntGenerator.class), List.class);
        }

    };

    @Test
    public void shouldGenerateAClass() throws ClassNotFoundException {
        TestClass testCase = new SampleTestClassGenerator(gen).generate();

        Class<?> clazz = testCase.load();
        Result result = JUnitCore.runClasses(clazz);

        assertThat(result.getFailureCount(), equalTo(0));
    }
}
