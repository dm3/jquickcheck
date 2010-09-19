package lt.dm3.jquickcheck.api;


public interface RequestToSynthesize<G> {

    G synthesize(Synthesizer<G> synth, GeneratorRepository<G> repo);

}
