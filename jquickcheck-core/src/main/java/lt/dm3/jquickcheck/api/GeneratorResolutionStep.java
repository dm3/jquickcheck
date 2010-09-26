package lt.dm3.jquickcheck.api;

import lt.dm3.jquickcheck.api.impl.resolution.NamedAndTypedGenerator;


public interface GeneratorResolutionStep<GEN> {
    Iterable<NamedAndTypedGenerator<GEN>> resolveFrom(Object context);
}
