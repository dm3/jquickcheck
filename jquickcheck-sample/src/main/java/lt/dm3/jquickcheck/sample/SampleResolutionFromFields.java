package lt.dm3.jquickcheck.sample;

import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.impl.lookup.DefaultLookupByType;
import lt.dm3.jquickcheck.api.impl.repo.GeneratorRepositoryBuilder;
import lt.dm3.jquickcheck.api.impl.resolution.CompositeResolution;
import lt.dm3.jquickcheck.api.impl.resolution.ResolutionFromFields;
import lt.dm3.jquickcheck.api.impl.resolution.ResolutionFromMethods;
import lt.dm3.jquickcheck.api.impl.resolution.TypedGenerator;

public class SampleResolutionFromFields extends CompositeResolution<Generator<?>> {

    @SuppressWarnings("unchecked")
    public SampleResolutionFromFields() {
        super(new ResolutionFromFields<Generator<?>>(Generator.class),
                new ResolutionFromMethods<Generator<?>>(Generator.class));
    }

    @Override
    protected GeneratorRepositoryBuilder<Generator<?>> createRepositoryBuilder(Object context) {
        return new GeneratorRepositoryBuilder<Generator<?>>(context) {

            @Override
            protected LookupDefaultByType<Generator<?>> createLookupDefaultByType(
                Iterable<TypedGenerator<Generator<?>>> defaults, Object context) {
                return new SampleLookupDefaultByType(DefaultLookupByType.from(defaults));
            }

        };
    }

}
