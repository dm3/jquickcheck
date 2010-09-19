package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.RequestToSynthesize;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.internal.Primitives;

/**
 * Stores generators identified by the {name, type} tuple.
 * <p>
 * Does not allow several generators with the same identifiers.
 * 
 * @author dm3
 * 
 * @param <G>
 */
public abstract class DefaultGeneratorRepository<G> implements GeneratorRepository<G> {
    // TODO: put into an unmodifiable map
    private final Map<String, Set<G>> nameToGenerator = new HashMap<String, Set<G>>();
    private final Map<Type, Set<G>> typeToGenerator = new HashMap<Type, Set<G>>();
    private final Map<DefaultGeneratorId, G> idToGenerator = new HashMap<DefaultGeneratorId, G>();
    private final Synthesizer<G> synthesizer;

    protected DefaultGeneratorRepository(Iterable<? extends NamedAndTypedGenerator<G>> generators,
            Synthesizer<G> synthesizer) {
        this.synthesizer = synthesizer;
        for (NamedAndTypedGenerator<G> gen : generators) {
            DefaultGeneratorId id = new DefaultGeneratorId(gen.getName(), gen.getType());
            if (idToGenerator.containsKey(id)) {
                throw new IllegalArgumentException("Duplicate generator for id: " + id);
            }
            idToGenerator.put(id, gen.getGenerator());
            putByName(gen);
            if (gen.getType() != null) {
                putByType(gen);
            }
        }
    }

    private void putByName(NamedAndTypedGenerator<G> gen) {
        Set<G> existing = nameToGenerator.get(gen.getName());
        if (existing == null) {
            existing = new HashSet<G>();
        }
        existing.add(gen.getGenerator());
        nameToGenerator.put(gen.getName(), existing);
    }

    private void putByType(NamedAndTypedGenerator<G> gen) {
        Set<G> existing = typeToGenerator.get(gen.getType());
        if (existing == null) {
            existing = new HashSet<G>();
        }
        existing.add(gen.getGenerator());
        if (Primitives.isPrimitiveOrWrapper(gen.getType())) {
            typeToGenerator.put(Primitives.oppositeOf(gen.getType()), existing);
        }
        typeToGenerator.put(gen.getType(), existing);
    }

    @Override
    public boolean hasGeneratorFor(String name) {
        Set<G> forName = nameToGenerator.get(name);
        if (forName != null && forName.size() > 1) {
            throw new IllegalArgumentException("More than one generator exists for name " + name + ": " + forName);
        }
        return nameToGenerator.containsKey(name);
    }

    @Override
    public G getGeneratorFor(String name) {
        if (!hasGeneratorFor(name)) {
            throw new IllegalArgumentException("No generator found with name " + name);
        }
        return nameToGenerator.get(name).iterator().next();
    }

    @Override
    public boolean hasGeneratorFor(Type t) {
        Set<G> forType = typeToGenerator.get(t);
        if (forType != null && forType.size() > 1) {
            throw new IllegalArgumentException("More than one generator exists for type " + t + ": " + forType);
        }
        return typeToGenerator.containsKey(t);
    }

    @Override
    public G getGeneratorFor(Type t) {
        if (!hasGeneratorFor(t)) {
            throw new IllegalArgumentException("No generator found for type " + t);
        }
        return typeToGenerator.get(t).iterator().next();
    }

    @Override
    public G getSyntheticGeneratorFor(RequestToSynthesize<G> request) {
        return request.synthesize(synthesizer, this);
    }

    @Override
    public abstract G getDefaultGeneratorFor(Type t);

}