package lt.dm3.jquickcheck.api;

/**
 * Request which is issued when a generator must be synthesized instead of directly queried from a repository.
 * 
 * @author dm3
 * 
 * @param <GEN>
 *            type of the generator synthesized by this request
 */
public interface RequestToSynthesize<GEN> {

    /**
     * @param repo
     *            repository of generators for the given type
     * @return a generator synthesized by this request
     */
    GEN synthesize(GeneratorRepository<GEN> repo);

}
