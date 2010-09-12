package lt.dm3.jquickcheck.test.builder;

public interface TestClassBuilderFactory<T> {

    AbstractTestClassBuilder<T> createBuilder(String className, Class<T> generatorClass);
}
