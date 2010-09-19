package lt.dm3.jquickcheck.api;

import java.lang.reflect.ParameterizedType;

public interface RequestToSynthesize<G> {

    G synthesize(ParameterizedType t, Synthesizer<G> synth, GeneratorRepository<G> repo);

}
