package lt.dm3.jquickcheck.test;

public class GeneratorInfo {

    private final String generatorValue;
    private final Class<?> generatedValue;

    public GeneratorInfo(String generatorValue, Class<?> generatedValue) {
        this.generatorValue = generatorValue;
        this.generatedValue = generatedValue;
    }

    public String getGeneratorValue() {
        return generatorValue;
    }

    public Class<?> getGeneratedValue() {
        return generatedValue;
    }

}
