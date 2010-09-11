package lt.dm3.jquickcheck.test.builder;

import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.test.builder.AbstractTestClassBuilder;
import lt.dm3.jquickcheck.test.builder.GeneratorInfo;
import lt.dm3.jquickcheck.test.builder.TestClassBuilderFactory;
import lt.dm3.jquickcheck.test.builder.TestClassGenerator;

public class SampleTestClassGenerator extends TestClassGenerator<Generator<?>> {

    public SampleTestClassGenerator(Generator<GeneratorInfo> generatorGenerator) {
        super(generatorGenerator, new TestClassBuilderFactory<Generator<?>>() {
            public AbstractTestClassBuilder<Generator<?>> createBuilder(String className,
                                                                        Class<? super Generator<?>> generatorClass) {
                return SampleTestClassBuilder.forSample(className, generatorClass);
            }
        });
    }

    @Override
    protected Class<? super Generator<?>> getGeneratorClass() {
        return Generator.class;
    }

}
