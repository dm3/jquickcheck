package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.LookupSynthetic;

public class DefaultGeneratorRepository<GEN> implements GeneratorRepository<GEN> {

    private final Lookup<String, GEN> byName;
    private final Lookup<Type, GEN> byType;
    private final LookupDefaultByType<GEN> defaultByType;
    private final LookupSynthetic<GEN> synthetic;

    public DefaultGeneratorRepository(Lookup<String, GEN> byName, Lookup<Type, GEN> byType,
                                        LookupDefaultByType<GEN> defaultByType,
                                        LookupSynthetic<GEN> lookupSynthetic) {
        this.byName = byName;
        this.byType = byType;
        this.defaultByType = defaultByType;
        this.synthetic = lookupSynthetic;
    }

    @Override
    public boolean has(String name) {
        return byName.has(name);
    }

    public boolean hasOne(String name) {
        return byName.hasOne(name);
    }

    @Override
    public GEN get(String name) {
        return byName.get(name);
    }

    public Set<GEN> getAll(String name) {
        return byName.getAll(name);
    }

    @Override
    public boolean has(Type t) {
        return byType.has(t);
    }

    @Override
    public GEN get(Type t) {
        return byType.get(t);
    }

    @Override
    public boolean hasDefault(Type t) {
        return defaultByType.hasDefault(t);
    }

    @Override
    public GEN getDefault(Type t) {
        return defaultByType.getDefault(t);
    }

    @Override
    public boolean hasSynthetic(Type t) {
        return false;
    }

    @Override
    public GEN getSyntheticGeneratorFor(Type type, List<GEN> components) {
        return synthetic.getSynthetic(type);
    }

    public GEN get(String name, Type type) {
        Set<GEN> gensByType = byType.getAll(type);
        GEN result = null;
        for (GEN n : byName.getAll(name)) {
            for (GEN t : gensByType) {
                if (n.equals(t)) {
                    if (result != null) {
                        throw noUniqueGenerators(name, type);
                    }
                    result = n;
                }
            }
        }
        if (result == null) {
            throw noUniqueGenerators(name, type);
        }
        return result;
    }

    private IllegalArgumentException noUniqueGenerators(String name, Type type) {
        return new IllegalArgumentException(String.format("No unique generators found for (%s, %s)",
                name, type));
    }

}
