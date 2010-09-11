package lt.dm3.jquickcheck.test;

import java.util.Iterator;

import lt.dm3.jquickcheck.test.builder.AbstractTestClassBuilder;
import lt.dm3.jquickcheck.test.builder.ClassUtils;
import lt.dm3.jquickcheck.test.builder.GeneratedTest;
import lt.dm3.jquickcheck.test.builder.GeneratorInfo;
import lt.dm3.jquickcheck.test.builder.RandomUtils;
import lt.dm3.jquickcheck.test.builder.TestClassBuilderFactory;

public abstract class AbstractCommonScenarioTest<T> {

    protected abstract TestClassBuilderFactory<T> defaultClassBuilderFactory();

    protected abstract Class<T> generatorClass();

    protected abstract Iterable<GeneratorInfo> supportedGenerators();

    protected GeneratedTest withOneNoArgPropertyReturningTrue() {
        AbstractTestClassBuilder<T> b = defaultClassBuilderFactory().createBuilder(RandomUtils.randomJavaIdentifier(),
                                                                                   generatorClass());
        return b.withProperty(RandomUtils.randomJavaIdentifier(), true).build();
    }

    protected GeneratedTest withOneNoArgPropertyReturningFalse() {
        AbstractTestClassBuilder<T> b = defaultClassBuilderFactory().createBuilder(RandomUtils.randomJavaIdentifier(),
                                                                                   generatorClass());
        return b.withProperty(RandomUtils.randomJavaIdentifier(), false).build();
    }

    protected Iterable<GeneratedTest> withOneFieldAndOnePropertyAndDifferentFieldModifiers() {
        return new Iterable<GeneratedTest>() {
            public Iterator<GeneratedTest> iterator() {
                return new Iterator<GeneratedTest>() {
                    private final int[] modifiers = AllModifiers.toArray();
                    private final Iterator<GeneratorInfo> generators = supportedGenerators().iterator();
                    private int current = 0;

                    public boolean hasNext() {
                        return modifiers.length > current;
                    }

                    public GeneratedTest next() {
                        AbstractTestClassBuilder<T> b = defaultClassBuilderFactory()
                                .createBuilder(RandomUtils.randomJavaIdentifier(), generatorClass());
                        GeneratorInfo gen = generators.next();
                        /*
                         * This is brittle. If generator wouldn't be added to this class and the default generators for a 
                         * provider would provide the test case with a generator required to run the propery, test
                         * would still pass. We need to disable the usage of default generators and hope that the
                         * test runner honors our settings.
                         */
                        b.withGenerator(modifiers[current], ClassUtils.describe(gen.getGeneratedValue()), "one",
                                        gen.getGeneratorValue());
                        b.withProperty(RandomUtils.randomJavaIdentifier(), true,
                                       ClassUtils.describe(gen.getGeneratedValue()));
                        current++;
                        return b.build();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException("Cannot remove!");
                    }
                };
            }
        };
    }
}
