package lt.dm3.jquickcheck.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Iterator;

import lt.dm3.jquickcheck.Property;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public abstract class AbstractDefaultGeneratorTest<T> {

    protected abstract Iterable<GeneratorInfo> defaultGenerators();

    protected abstract TestClassBuilderFactory<T> defaultClassBuilderFactory();

    protected abstract Class<T> generatorClass();

    @Test
    public void shouldResolveAllOfTheDefaultGenerators() {
        Iterator<TestClass> i = new DefaultGeneratorsTestClassIterator<T>(defaultGenerators().iterator(),
                                                                          defaultClassBuilderFactory(),
                                                                          generatorClass(), 5, 8);
        while (i.hasNext()) {
            TestClass clazz = i.next();
            Class<?> loaded = clazz.load();

            Result result = JUnitCore.runClasses(loaded);
            int totalTests = new org.junit.runners.model.TestClass(loaded).getAnnotatedMethods(Property.class).size();

            assertThat(result.getFailureCount(), equalTo(0));
            assertThat(result.getRunCount(), equalTo(totalTests));
        }
    }
}
