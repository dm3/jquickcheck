package lt.dm3.jquickcheck.test;

import java.util.Random;

import javassist.Modifier;
import javassist.bytecode.Descriptor;
import lt.dm3.jquickcheck.sample.Generator;

public abstract class TestClassGenerator<T> implements Generator<TestClass> {

    private final Random r = new Random();
    private final Generator<GeneratorInfo> generatorGenerator;
    private static final int maxGenerators = 20, maxParams = 8, maxProperties = 20;
    private static final int[] modifiers = new int[] { Modifier.PUBLIC, Modifier.PRIVATE, Modifier.PROTECTED,
                                                Modifier.STATIC,
                                                Modifier.FINAL,
                                                Modifier.setPackage(Modifier.STATIC),
                                                Modifier.setPackage(Modifier.FINAL),
                                                Modifier.setPrivate(Modifier.STATIC),
                                                Modifier.setPrivate(Modifier.FINAL),
                                                Modifier.setProtected(Modifier.STATIC),
                                                Modifier.setProtected(Modifier.FINAL),
                                                Modifier.setPublic(Modifier.STATIC),
                                                Modifier.setPublic(Modifier.FINAL) };
    private final TestClassBuilderFactory<? super T> builderFactory;

    public TestClassGenerator(Generator<GeneratorInfo> generatorGenerator,
            TestClassBuilderFactory<? super T> builderFactory) {
        this.generatorGenerator = generatorGenerator;
        this.builderFactory = builderFactory;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public TestClass generate() {
        AbstractTestClassBuilder<? super T> builder = builderFactory.createBuilder(String.valueOf(RandomUtils
                .randomJavaIdentifier()),
                                                                                      (Class) getGeneratorClass());
        int generators = r.nextInt(maxGenerators);
        GeneratorInfo[] gens = new GeneratorInfo[generators];
        String[] names = new String[generators];
        for (int i = 0; i < generators; i++) {
            int mod = modifiers[r.nextInt(modifiers.length)];
            GeneratorInfo generator = generatorGenerator.generate();
            String name = RandomUtils.randomJavaIdentifier();
            gens[i] = generator;
            names[i] = name;
            builder.withGenerator(mod, ClassUtils.describe(generator.getGeneratedValue()), name,
                                  generator.getGeneratorValue());
        }
        int properties = r.nextInt(maxProperties - 1) + 1; // cannot have 0 properties
        for (int i = 0; i < properties; i++) {
            String propName = RandomUtils.randomJavaIdentifier();
            int params = r.nextInt(Math.min(maxParams, generators + 1));
            String[] paramClasses = new String[params];
            for (int j = 0; j < params; j++) {
                int gen = r.nextInt(j + 1);
                paramClasses[j] = ClassUtils.describe(gens[gen].getGeneratedValue());

                /*
                 * if (r.nextBoolean()) { // add G(gen = name) annotation String generatorName = names[gen]; } else {
                 * 
                 * }
                 */
            }
            builder.withProperty(propName, paramClasses);
        }
        return builder.build();
    }

    protected abstract Class<? super T> getGeneratorClass();

    private String descriptorOf(Class<?> clazz) {
        return Descriptor.of(clazz.getName());
    }

}
