package lt.dm3.jquickcheck.api.impl.resolution;


public interface GeneratorResolutionStep<GEN> {
    Iterable<NamedAndTypedGenerator<GEN>> resolveFrom(Object context);
}
