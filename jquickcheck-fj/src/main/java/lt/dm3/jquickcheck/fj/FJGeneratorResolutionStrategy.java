package lt.dm3.jquickcheck.fj;

import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.api.impl.lookup.DefaultLookupByType;
import lt.dm3.jquickcheck.api.impl.repo.GeneratorRepositoryBuilder;
import lt.dm3.jquickcheck.api.impl.resolution.CompositeResolution;
import lt.dm3.jquickcheck.api.impl.resolution.ResolutionFromFields;
import lt.dm3.jquickcheck.api.impl.resolution.ResolutionFromMethods;
import lt.dm3.jquickcheck.api.impl.resolution.TypedGenerator;
import fj.test.Arbitrary;

public class FJGeneratorResolutionStrategy extends CompositeResolution<Arbitrary<?>> {

    @SuppressWarnings("unchecked")
    public FJGeneratorResolutionStrategy() {
        super(new FJImplicitResolution(),
                new ResolutionFromFields<Arbitrary<?>>(Arbitrary.class),
                new ResolutionFromMethods<Arbitrary<?>>(Arbitrary.class));
    }

    @Override
    protected GeneratorRepositoryBuilder<Arbitrary<?>> createRepositoryBuilder(Object context) {
        return new GeneratorRepositoryBuilder<Arbitrary<?>>(context) {

            @Override
            protected LookupDefaultByType<Arbitrary<?>> createLookupDefaultByType(
                Iterable<TypedGenerator<Arbitrary<?>>> defaults, Object context) {
                return new FJLookupDefaultByType(DefaultLookupByType.from(defaults));
            }

            @Override
            protected Synthesizer<Arbitrary<?>> createSynthesizer(Object context) {
                return new FJSynthesizer();
            }

        };
    }

}
