package lt.dm3.jquickcheck.test;

import lt.dm3.jquickcheck.junit4.JUnitTestClassBuilder;
import lt.dm3.jquickcheck.qc.QC;
import lt.dm3.jquickcheck.test.builder.AbstractTestClassBuilder;
import lt.dm3.jquickcheck.test.builder.TestClassBuilderFactory;
import net.java.quickcheck.Generator;

public class JUnitQCFactory implements TestClassBuilderFactory<Generator<?>> {

    @Override
    public AbstractTestClassBuilder<Generator<?>> createBuilder(String className, Class<Generator<?>> generatorClass) {
        return new JUnitTestClassBuilder<Generator<?>>(className, generatorClass) {
            @Override
            protected Class<?> getQuickCheckProviderClass() {
                return QC.class;
            }
        };
    }

}
