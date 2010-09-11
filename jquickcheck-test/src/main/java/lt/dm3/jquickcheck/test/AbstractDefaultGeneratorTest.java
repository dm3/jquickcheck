package lt.dm3.jquickcheck.test;

import java.util.Iterator;

import lt.dm3.jquickcheck.test.builder.DefaultGeneratorsTestClassIterator;
import lt.dm3.jquickcheck.test.builder.GeneratorInfo;
import lt.dm3.jquickcheck.test.builder.GeneratedTest;
import lt.dm3.jquickcheck.test.builder.TestClassBuilderFactory;

public abstract class AbstractDefaultGeneratorTest<T> {

    protected abstract Iterable<GeneratorInfo> defaultGenerators();

    protected abstract TestClassBuilderFactory<T> defaultClassBuilderFactory();

    protected abstract Class<T> generatorClass();

    protected Iterator<GeneratedTest> createTestClassIterator(int maxProperties, int maxParameters) {
        return new DefaultGeneratorsTestClassIterator<T>(defaultGenerators().iterator(),
                                                         defaultClassBuilderFactory(),
                                                         generatorClass(), 5, 8);
    }
}
