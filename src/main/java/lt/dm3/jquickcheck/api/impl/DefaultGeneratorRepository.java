package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.internal.Primitives;

public abstract class DefaultGeneratorRepository<G> implements GeneratorRepository<G> {
    private final Map<String, G> nameToGenerator = new HashMap<String, G>();
    private final Map<Type, G> typeToGenerator = new HashMap<Type, G>();

    protected DefaultGeneratorRepository(Iterable<? extends NamedAndTypedGenerator<G>> generators) {
        for (NamedAndTypedGenerator<G> gen : generators) {
            nameToGenerator.put(gen.getName(), gen.getGenerator());
            if (gen.getType() != null) {
                if (Primitives.isPrimitiveOrWrapper(gen.getType())) {
                    typeToGenerator.put(Primitives.oppositeOf(gen.getType()), gen.getGenerator());
                }
                typeToGenerator.put(gen.getType(), gen.getGenerator());
            }
        }
    }

    @Override
    public boolean hasGeneratorFor(String name) {
        return nameToGenerator.containsKey(name);
    }

    @Override
    public G getGeneratorFor(String name) {
        if (!hasGeneratorFor(name)) {
            throw new IllegalArgumentException("No generator found with name " + name);
        }
        return nameToGenerator.get(name);
    }

    @Override
    public boolean hasGeneratorFor(Type t) {
        return typeToGenerator.containsKey(t);
    }

    @Override
    public G getGeneratorFor(Type t) {
        if (!hasGeneratorFor(t)) {
            throw new IllegalArgumentException("No generator found for type " + t);
        }
        return typeToGenerator.get(t);
    }

    @Override
    public abstract G getDefaultGeneratorFor(Type t);

}