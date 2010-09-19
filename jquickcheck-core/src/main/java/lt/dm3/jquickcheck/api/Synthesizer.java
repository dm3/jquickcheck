package lt.dm3.jquickcheck.api;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public interface Synthesizer<G> {

    /**
     * Synthesizes a Generator which generates values of the given <tt>type</tt>.
     * 
     * @param t
     * @param components
     * @return
     */
    G synthesize(ParameterizedType t, List<G> components);

}
