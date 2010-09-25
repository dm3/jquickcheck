package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.impl.resolution.NamedAndTypedGenerator;

public class GeneratorHolder implements NamedAndTypedGenerator<Generator<?>> {

    private final Type type;
    private final String name;
    private final Generator<?> generator;

    public GeneratorHolder(Type type, String name, Generator<?> generator) {
        this.type = type;
        this.name = name;
        this.generator = generator;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Generator<?> getGenerator() {
        return generator;
    }

}
