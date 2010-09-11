package lt.dm3.jquickcheck.test.builder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * Creates a testcase for a specified runner for each N default generators.
 * 
 * @author dm3
 * 
 */
public class DefaultGeneratorsTestClassIterator<T> implements Iterator<GeneratedTest> {

    private final TestClassBuilderFactory<T> builderFactory;
    private final Iterator<GeneratorInfo> defaultGenerators;
    private final Class<T> genClass;
    private final int maxGeneratorsPerProperty;
    private final int maxProperties;
    private final Random r = new Random();

    public DefaultGeneratorsTestClassIterator(Iterator<GeneratorInfo> defaultGenerators,
            TestClassBuilderFactory<T> builderFactory, Class<T> genClass, int maxGenerators, int maxProperties) {
        this.defaultGenerators = defaultGenerators;
        this.builderFactory = builderFactory;
        this.genClass = genClass;
        this.maxGeneratorsPerProperty = maxGenerators;
        this.maxProperties = maxProperties;
    }

    public GeneratedTest next() {
        AbstractTestClassBuilder<T> b = builderFactory.createBuilder(RandomUtils.randomJavaIdentifier(), genClass);
        int properties = r.nextInt(maxGeneratorsPerProperty) + 1;
        boolean finished = false;
        for (int i = 0; i < properties && !finished; i++) {
            int arguments = r.nextInt(maxProperties) + 1;
            Parameter[] values = new Parameter[arguments];
            for (int j = 0; j < arguments && !finished; j++) {
                if (defaultGenerators.hasNext()) {
                    values[j] = Parameter.of(defaultGenerators.next().getGeneratedValue());
                } else {
                    finished = true;
                    values = Arrays.copyOf(values, j);
                }
            }
            b.withRandomProperty().with(values).and();
        }
        return b.useDefaults().build();
    }

    public boolean hasNext() {
        return defaultGenerators.hasNext();
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove!");
    }
}
