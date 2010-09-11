package lt.dm3.jquickcheck.test.builder;

import java.lang.reflect.Type;

public class GeneratorInfo {

    private final String generatorValue;
    private final Type generatedValue;

    public GeneratorInfo(String generatorValue, Type generatedValue) {
        this.generatorValue = generatorValue;
        this.generatedValue = generatedValue;
    }

    public String getGeneratorValue() {
        return generatorValue;
    }

    public Type getGeneratedValue() {
        return generatedValue;
    }

}
