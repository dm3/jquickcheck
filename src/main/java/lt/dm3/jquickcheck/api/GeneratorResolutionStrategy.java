package lt.dm3.jquickcheck.api;

public interface GeneratorResolutionStrategy<G> {

    <T> GeneratorRepository<G> resolve(T context);

}
