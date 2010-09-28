package lt.dm3.jquickcheck.api.impl.repo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.LookupSynthetic;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.api.impl.DefaultSynthesizer;
import lt.dm3.jquickcheck.api.impl.DefaultSynthesizer.Synthesized;
import lt.dm3.jquickcheck.api.impl.lookup.DefaultLookupByName;
import lt.dm3.jquickcheck.api.impl.lookup.DefaultLookupByType;
import lt.dm3.jquickcheck.api.impl.lookup.DefaultLookupSynthetic;
import lt.dm3.jquickcheck.api.impl.lookup.LookupByTypeThenDefault;
import lt.dm3.jquickcheck.api.impl.resolution.NamedAndTypedGenerator;
import lt.dm3.jquickcheck.api.impl.resolution.NamedGenerator;
import lt.dm3.jquickcheck.api.impl.resolution.TypedGenerator;

public class GeneratorRepositoryBuilder<GEN> {

    private final Object lookupContext;
    // workaround for not being able to pass List<? super NamedAndTypedGenerator<GEN>> into
    // method(List<NamedGenerator<GEN>>)
    private final List<NamedGenerator<GEN>> namedGenerators = new ArrayList<NamedGenerator<GEN>>();
    private final List<TypedGenerator<GEN>> typedGenerators = new ArrayList<TypedGenerator<GEN>>();
    private final List<TypedGenerator<GEN>> defaultGenerators = new ArrayList<TypedGenerator<GEN>>();

    public GeneratorRepositoryBuilder(Object lookupContext) {
        this.lookupContext = lookupContext;
    }

    public GeneratorRepositoryBuilder<GEN> add(NamedAndTypedGenerator<GEN> generator) {
        if (generator.isDefault()) {
            defaultGenerators.add(generator);
        } else {
            namedGenerators.add(generator);
            typedGenerators.add(generator);
        }
        return this;
    }

    public GeneratorRepositoryBuilder<GEN> addAll(Iterable<NamedAndTypedGenerator<GEN>> generators) {
        for (NamedAndTypedGenerator<GEN> g : generators) {
            this.add(g);
        }
        return this;
    }

    public GeneratorRepository<GEN> build() {
        Lookup<Type, GEN> byType = createLookupByType(typedGenerators);
        Lookup<String, GEN> byName = createLookupByName(namedGenerators);
        LookupDefaultByType<GEN> byDefault = createLookupDefaultByType(defaultGenerators, lookupContext);
        Lookup<Type, GEN> byTypeAndDefault = new LookupByTypeThenDefault<GEN>(byType, byDefault);
        Synthesizer<GEN> synth = createSynthesizer(lookupContext);
        LookupSynthetic<GEN> bySynthetic = new DefaultLookupSynthetic<GEN>(synth, byTypeAndDefault);
        return new DefaultGeneratorRepository<GEN>(byName, byType, byDefault, bySynthetic);
    }

    private static class SomeDefaultLookup<GEN> implements LookupDefaultByType<GEN> {
        private final Lookup<Type, GEN> byType;

        public SomeDefaultLookup(Lookup<Type, GEN> byType) {
            this.byType = byType;
        }

        @Override
        public boolean hasDefault(Type t) {
            return byType.has(t);
        }

        @Override
        public GEN getDefault(Type t) {
            return byType.get(t);
        }
    }

    protected Lookup<String, GEN> createLookupByName(Iterable<NamedGenerator<GEN>> namedGenerators) {
        return DefaultLookupByName.from(namedGenerators);
    }

    protected Lookup<Type, GEN> createLookupByType(Iterable<TypedGenerator<GEN>> typedGenerators) {
        return DefaultLookupByType.from(typedGenerators);
    }

    protected LookupDefaultByType<GEN> createLookupDefaultByType(Iterable<TypedGenerator<GEN>> defaults, Object context) {
        return new SomeDefaultLookup<GEN>(DefaultLookupByType.from(defaults));
    }

    protected Synthesizer<GEN> createSynthesizer(Object context) {
        return new DefaultSynthesizer<GEN>(Collections.<Synthesized<GEN>> emptyList());
    }

}
