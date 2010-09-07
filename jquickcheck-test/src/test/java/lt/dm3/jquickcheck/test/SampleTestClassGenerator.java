package lt.dm3.jquickcheck.test;

import lt.dm3.jquickcheck.sample.Generator;

public class SampleTestClassGenerator extends TestClassGenerator<Generator<?>> {

    public SampleTestClassGenerator(Generator<GeneratorInfo> generatorGenerator) {
        super(generatorGenerator);
    }

    @Override
    protected Class<? super Generator<?>> getGeneratorClass() {
        return Generator.class;
    }

}
