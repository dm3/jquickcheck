package lt.dm3.jquickcheck.test;

import lt.dm3.jquickcheck.sample.Generator;

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
