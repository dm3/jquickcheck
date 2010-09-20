package lt.dm3.jquickcheck.api;

/**
 * Request which is issued when a generator must be synthesized instead of directly queried from a repository.
 * 
 * @author dm3
 * 
 * @param <G>
 *            type of the generator synthesized by this request
 */
public interface RequestToSynthesize<G> {

    /**
     * @param synth
     *            synthesizer of generators for the given type
     * @param repo
     *            repository of generators for the given type
     * @return a generator synthesized by this request
     */
    G synthesize(Synthesizer<G> synth, GeneratorRepository<G> repo);

}
