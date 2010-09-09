package lt.dm3.jquickcheck.test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;

import org.junit.Test;

public class TestClassGeneratorTest {

    private final Generator<GeneratorInfo> gen = new Generator<GeneratorInfo>() {
        private final Random r = new Random();

        public GeneratorInfo generate() {
            int next = r.nextInt();
            if (next % 3 == 0) {
                return new GeneratorInfo(ClassUtils.newInstance(IntegerGenerator.class), Integer.class);
            } else if (next % 2 == 0) {
                return new GeneratorInfo("lt.dm3.jquickcheck.test.TestClassGeneratorTest.staticMethod();", List.class);
            } else {
                return new GeneratorInfo(ClassUtils.newInstance(ListIntGenerator.class), List.class);
            }
        }

    };

    public static Generator<List<Integer>> staticMethod() {
        return new ListIntGenerator();
    }

    @Test
    public void shouldGenerateAValidClass() throws ClassNotFoundException, InstantiationException,
        IllegalAccessException {
        for (int i = 0; i < 100; i++) {
            TestClass testCase = new SampleTestClassGenerator(gen).generate();

            Class<?> clazz = testCase.load();

            Object instance = clazz.newInstance();
            for (Method m : clazz.getDeclaredMethods()) {
                assertThat(m.getAnnotation(Property.class), not(nullValue()));
            }
            for (Field f : clazz.getDeclaredFields()) {
                assertThat(f.getType(), typeCompatibleWith(Generator.class));
            }
        }
    }
}
