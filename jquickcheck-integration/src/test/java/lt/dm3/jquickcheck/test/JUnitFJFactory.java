package lt.dm3.jquickcheck.test;

import lt.dm3.jquickcheck.fj.FJ;
import lt.dm3.jquickcheck.junit4.JUnitTestClassBuilder;
import lt.dm3.jquickcheck.test.builder.AbstractTestClassBuilder;
import lt.dm3.jquickcheck.test.builder.TestClassBuilderFactory;
import fj.test.Arbitrary;

public class JUnitFJFactory implements TestClassBuilderFactory<Arbitrary<?>> {
    @Override
    public AbstractTestClassBuilder<Arbitrary<?>> createBuilder(String className, Class<Arbitrary<?>> generatorClass) {
        return new JUnitTestClassBuilder<Arbitrary<?>>(className, generatorClass) {
            @Override
            protected Class<?> getQuickCheckProviderClass() {
                return FJ.class;
            }
        };
    }
}
