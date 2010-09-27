package lt.dm3.jquickcheck.qc;

import lt.dm3.jquickcheck.api.impl.resolution.CompositeResolution;
import lt.dm3.jquickcheck.api.impl.resolution.ResolutionFromFields;
import lt.dm3.jquickcheck.api.impl.resolution.ResolutionFromMethods;
import net.java.quickcheck.Generator;

public class QCGeneratorResolutionStrategy extends CompositeResolution<Generator<?>> {

    @SuppressWarnings("unchecked")
    public QCGeneratorResolutionStrategy() {
        super(new ResolutionFromFields<Generator<?>>(Generator.class),
                new ResolutionFromMethods<Generator<?>>(Generator.class));
    }

}
