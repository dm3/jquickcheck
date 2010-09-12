package lt.dm3.jquickcheck.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.util.Iterator;

import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.fj.DefaultGenerators;
import lt.dm3.jquickcheck.test.builder.GeneratedTest;
import lt.dm3.jquickcheck.test.builder.GeneratorInfo;
import lt.dm3.jquickcheck.test.builder.TestClassBuilderFactory;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.TestClass;

import fj.test.Arbitrary;

/**
 * Tests default generator resolution in JUnit + FJ setting.
 * 
 * @author dm3
 * 
 */
public class JUnitFJDefaultGeneratorTest extends AbstractDefaultGeneratorTest<Arbitrary<?>> {

    @Override
    protected TestClassBuilderFactory<Arbitrary<?>> defaultClassBuilderFactory() {
        return new JUnitFJFactory();
    }

    @Override
    protected Iterable<GeneratorInfo> defaultGenerators() {
        return new DefaultGenerators();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<Arbitrary<?>> generatorClass() {
        return (Class) Arbitrary.class;
    }

    @Test
    public void shouldResolveAllOfTheDefaultGenerators() {
        Iterator<GeneratedTest> i = createTestClassIterator(5, 8);
        while (i.hasNext()) {
            GeneratedTest clazz = i.next();
            Class<?> loaded = clazz.load();

            Result result = JUnitCore.runClasses(loaded);
            int totalTests = new TestClass(loaded).getAnnotatedMethods(Property.class).size();

            assertThat(totalTests, greaterThan(0));
            assertThat(result.getFailureCount(), equalTo(0));
            assertThat(result.getRunCount(), equalTo(totalTests));
        }
    }

}
