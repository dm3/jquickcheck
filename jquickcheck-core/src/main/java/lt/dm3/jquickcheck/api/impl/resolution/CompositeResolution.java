package lt.dm3.jquickcheck.api.impl.resolution;

import java.util.Arrays;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStep;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.api.impl.GeneratorRepositoryBuilder;

/**
 * The generators are resolved from
 * <ol>
 * <li>Fields defined in the test case</li>
 * <li>Methods returning and instance of a generator defined in the test case</li>
 * <li>The set of default generators supplied by a quickcheck implementation provider (if the corresponding setting is
 * true)</li>
 * <li>The set of synthetic generators supplied by a quickcheck implementation provider (if the corresponding setting is
 * true)</li>
 * </ol>
 * 
 * @author dm3
 * 
 * @param <GEN>
 */
public final class CompositeResolution<GEN> implements GeneratorResolutionStrategy<GEN> {

    private final ResolutionOfImplicits<GEN> implicits;
    private final List<GeneratorResolutionStep<GEN>> steps;

    public CompositeResolution(ResolutionOfImplicits<GEN> implicits, GeneratorResolutionStep<GEN>... steps) {
        this.implicits = implicits;
        this.steps = Arrays.asList(steps);
    }

    public CompositeResolution(GeneratorResolutionStep<GEN>... steps) {
        this.implicits = null;
        this.steps = Arrays.asList(steps);
    }

    @Override
    public GeneratorRepository<GEN> resolve(final Object context) {
        GeneratorRepositoryBuilder<GEN> builder = new GeneratorRepositoryBuilder<GEN>(context);
        for (GeneratorResolutionStep<GEN> step : steps) {
            builder.addAll(step.resolveFrom(context));
        }
        if (implicits != null) {
            GeneratorRepository<GEN> normalsAndDefaults = builder.build();
            builder.addAll(implicits.resolveFrom(context, normalsAndDefaults));
        }
        return builder.build();
    }

}
