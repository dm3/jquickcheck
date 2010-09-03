package lt.dm3.jquickcheck;

public interface GeneratorResolutionStrategy<G> {

    <T> GeneratorRepository<G> resolve(T context);

}
