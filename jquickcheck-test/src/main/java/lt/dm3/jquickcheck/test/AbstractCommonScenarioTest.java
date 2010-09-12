package lt.dm3.jquickcheck.test;

import java.lang.reflect.Modifier;
import java.util.Iterator;

import javassist.CtClass;
import javassist.bytecode.Bytecode;
import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.test.builder.AbstractTestClassBuilder;
import lt.dm3.jquickcheck.test.builder.ClassUtils;
import lt.dm3.jquickcheck.test.builder.GeneratorInfo;
import lt.dm3.jquickcheck.test.builder.Parameter;
import lt.dm3.jquickcheck.test.builder.PropertyBuilder;
import lt.dm3.jquickcheck.test.builder.RandomUtils;
import lt.dm3.jquickcheck.test.builder.TestClassBuilderFactory;

import org.junit.Test;

public abstract class AbstractCommonScenarioTest<T> {

    protected abstract TestClassBuilderFactory<T> defaultClassBuilderFactory();

    protected abstract Class<T> generatorClass();

    protected abstract Iterable<GeneratorInfo> supportedGenerators();

    protected abstract GeneratorInfo positiveIntegerGenerator();

    protected abstract GeneratorInfo anyIntegerGenerator();

    /**
     * Implementor should check that the given class, when run in a provided runner, doesn't contain any failing
     * properties or otherwise incorrect data.
     * 
     * @param clazz
     */
    protected abstract void checkNoPropertiesFailIn(Class<?> clazz);

    protected abstract void checkAllPropertiesFailIn(Class<?> clazz);

    // tests
    @Test
    public void shouldResolveFieldsWithAnyCombinationOfModifiers() {
        int[] modifiers = AllModifiers.toArray();
        Iterator<GeneratorInfo> generators = supportedGenerators().iterator();
        int current = 0;
        while (current < modifiers.length) {
            // reinitialize generators, as we're testing all of the modifiers
            // we don't care even if there is only one generator in the iterator.
            if (!generators.hasNext()) {
                generators = supportedGenerators().iterator();
            }
            GeneratorInfo gen = generators.next();

            AbstractTestClassBuilder<T> b = defaultClassBuilderFactory().createBuilder(
                    RandomUtils.randomJavaIdentifier(),
                    generatorClass());
            /*
             * This is brittle. If generator wouldn't be added to this class and the default generators for a 
             * provider would provide the test case with a generator required to run the property, test
             * would still pass. We need to disable the usage of default generators and hope that the
             * test runner honors our settings.
             */
            b.withGenerator(modifiers[current++], ClassUtils.describe(gen.getGeneratedValue()), "one",
                            gen.getGeneratorValue());

            Class<?> clazz = b.withRandomProperty().with(Parameter.of(gen.getGeneratedValue())).and().build().load();

            checkNoPropertiesFailIn(clazz);
        }
    }

    @Test
    public void shouldResolveGeneratorsWithAnnotatedPropertyMethodParameters() {
        AbstractTestClassBuilder<T> b = defaultClassBuilderFactory().createBuilder(
                RandomUtils.randomJavaIdentifier(),
                generatorClass());

        GeneratorInfo gen = anyIntegerGenerator();
        b.withGenerator(Modifier.PUBLIC, ClassUtils.describe(gen.getGeneratedValue()), "anyInt",
                        gen.getGeneratorValue());
        gen = positiveIntegerGenerator();
        b.withGenerator(Modifier.PUBLIC, ClassUtils.describe(gen.getGeneratedValue()), "positiveInt",
                        gen.getGeneratorValue());

        PropertyBuilder<?> propB = b.withRandomProperty().with(
                Parameter.of(gen.getGeneratedValue()).annotatedBy(G.class).with("gen", "positiveInt").end());
        PropertyBuilder<?>.Body body = propB.initBody();
        // 1 parameter
        body.body.setMaxLocals(1);
        // return param > 0;
        body.body.addIload(1);
        body.body.add(Bytecode.IFLE, 8);
        body.body.addIconst(1);
        body.body.add(Bytecode.GOTO, 9);
        body.body.addIconst(0);
        body.body.addReturn(CtClass.booleanType);
        Class<?> clazz = propB.and().build().load();

        checkNoPropertiesFailIn(clazz);
    }

    @Test
    public void shouldCheckPropertyWithNoArguments() {
        AbstractTestClassBuilder<T> b = defaultClassBuilderFactory().createBuilder(RandomUtils.randomJavaIdentifier(),
                                                                                   generatorClass());
        Class<?> clazz = b.withRandomProperty().returning(true).and().build().load();

        checkNoPropertiesFailIn(clazz);
    }

    @Test
    public void shouldCheckPropertyWithNoArgumentsReturningFalse() {
        AbstractTestClassBuilder<T> b = defaultClassBuilderFactory().createBuilder(RandomUtils.randomJavaIdentifier(),
                                                                                   generatorClass());
        Class<?> clazz = b.withRandomProperty().returning(false).and().build().load();

        checkAllPropertiesFailIn(clazz);
    }

}
