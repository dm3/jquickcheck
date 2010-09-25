package lt.dm3.jquickcheck.api;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Synthesizes a Generator which generates values of the given <tt>type</tt>.
 * 
 * @author dm3
 * 
 * @param <G>
 *            type of the generator synthesized by this synthesizer
 */
public interface Synthesizer<G> {

    /**
     * Result of this method might be inconsistent with the actual result of calling {@link #synthesize(Type, List)}
     * with the same arguments as we can only be sure that synthesization succeeded when we actually perform it.
     * 
     * @param t
     * @param components
     * @return true if a generator can be created for a given type and its components
     */
    boolean canSynthesize(Type t, List<G> components);

    /**
     * Synthesizes a Generator which generates values of the given <tt>type</tt>.
     * 
     * @param t
     * @param components
     * @return
     */
    G synthesize(Type t, List<G> components);

}
