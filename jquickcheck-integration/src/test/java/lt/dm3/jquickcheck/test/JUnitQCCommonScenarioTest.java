package lt.dm3.jquickcheck.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.qc.DefaultGenerators;
import lt.dm3.jquickcheck.test.builder.GeneratorInfo;
import lt.dm3.jquickcheck.test.builder.TestClassBuilderFactory;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.PrimitiveGenerators;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.TestClass;

public class JUnitQCCommonScenarioTest extends AbstractCommonScenarioTest<Generator<?>> {

    @Override
    protected TestClassBuilderFactory<Generator<?>> defaultClassBuilderFactory() {
        return new JUnitQCFactory();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<Generator<?>> generatorClass() {
        return (Class) Generator.class;
    }

    @Override
    protected Iterable<GeneratorInfo> supportedGenerators() {
        return new DefaultGenerators();
    }

    @Override
    protected GeneratorInfo positiveIntegerGenerator() {
        return new GeneratorInfo(PrimitiveGenerators.class.getName() + ".integers(1);", Integer.class);
    }

    @Override
    protected GeneratorInfo anyIntegerGenerator() {
        return new GeneratorInfo(PrimitiveGenerators.class.getName() + ".integers();", Integer.class);
    }

    @Override
    protected void checkNoPropertiesFailIn(Class<?> clazz) {
        Result result = JUnitCore.runClasses(clazz);
        int totalTests = new TestClass(clazz).getAnnotatedMethods(Property.class).size();

        assertThat(result.getFailureCount(), equalTo(0));
        assertThat(result.getRunCount(), equalTo(totalTests));
    }

    @Override
    protected void checkAllPropertiesFailIn(Class<?> clazz) {
        Result result = JUnitCore.runClasses(clazz);
        int totalTests = new TestClass(clazz).getAnnotatedMethods(Property.class).size();

        assertThat(result.getRunCount(), equalTo(totalTests));
        assertThat(result.getFailureCount(), equalTo(totalTests));
        assertThat(result.getFailures().get(0).getException(), instanceOf(QuickCheckException.class));
    }

}
