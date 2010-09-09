package lt.dm3.jquickcheck.test;

import java.util.Iterator;

/**
 * Creates a testcase for a specified runner for each default generator.
 * 
 * @author dm3
 * 
 */
public abstract class DefaultGeneratorsTestClassIterator<T> implements Iterator<TestClass> {

    private final TestClassBuilderFactory<T> builderFactory;
    private final Iterator<GeneratorInfo> defaultGenerators;
    private final Class<T> genClass;

    public DefaultGeneratorsTestClassIterator(Iterator<GeneratorInfo> defaultGenerators,
            TestClassBuilderFactory<T> builderFactory, Class<T> genClass) {
        this.defaultGenerators = defaultGenerators;
        this.builderFactory = builderFactory;
        this.genClass = genClass;
    }

    public TestClass next() {
        AbstractTestClassBuilder<T> b = builderFactory.createBuilder("a", genClass);
        GeneratorInfo info = defaultGenerators.next();
        b.withProperty("defaultProperty", info.getGeneratedValue().getName());
        return b.build();
    }

    public boolean hasNext() {
        return defaultGenerators.hasNext();
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove!");
    }
}
