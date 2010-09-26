package lt.dm3.jquickcheck.api.impl.lookup;

import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.impl.resolution.NamedGenerator;

public class DefaultLookupByName<GEN> extends AbstractLookup<NamedGenerator<GEN>, String, GEN> {

    private DefaultLookupByName(Iterable<NamedGenerator<GEN>> generators) {
        for (NamedGenerator<GEN> gen : generators) {
            put(gen);
        }
    }

    @Override
    protected GEN generatorByContainer(NamedGenerator<GEN> container) {
        return container.getGenerator();
    }

    @Override
    protected String keyByContainer(NamedGenerator<GEN> container) {
        return container.getName();
    }

    public static <GEN> Lookup<String, GEN> from(Iterable<NamedGenerator<GEN>> gens) {
        return new DefaultLookupByName<GEN>(gens);
    }

}
