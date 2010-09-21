package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.internal.Primitives;

public class DefaultLookupByType<GEN> extends AbstractLookup<TypedGenerator<GEN>, Type, GEN> {

    private DefaultLookupByType(Iterable<TypedGenerator<GEN>> generators) {
        for (TypedGenerator<GEN> gen : generators) {
            if (gen.getType() != null) {
                put(gen);
            }
        }
    }

    @Override
    protected void putTo(Map<Type, Set<GEN>> values, TypedGenerator<GEN> container, Set<GEN> existing) {
        super.putTo(values, container, existing);
        if (Primitives.isPrimitiveOrWrapper(container.getType())) {
            values.put(Primitives.oppositeOf(container.getType()), existing);
        }
    }

    @Override
    protected GEN generatorByContainer(TypedGenerator<GEN> container) {
        return container.getGenerator();
    }

    @Override
    protected Type keyByContainer(TypedGenerator<GEN> container) {
        return container.getType();
    }

    public static <GEN> Lookup<Type, GEN> from(Iterable<TypedGenerator<GEN>> gens) {
        return new DefaultLookupByType<GEN>(gens);
    }

}
