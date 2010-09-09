package lt.dm3.jquickcheck.test;

public interface TestClassBuilderFactory<T> {

    AbstractTestClassBuilder<T> createBuilder(String className, Class<? super T> generatorClass);
}
