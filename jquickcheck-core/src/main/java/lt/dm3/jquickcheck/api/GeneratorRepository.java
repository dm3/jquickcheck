package lt.dm3.jquickcheck.api;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Repository for generators.
 * 
 * @author dm3
 * 
 * @param <G>
 *            type of the generator
 */
public interface GeneratorRepository<G> {

    /**
     * @param t
     *            type of the values generated by the generator
     * @return true if a generator which generates values of the given type exists in this repository
     */
    boolean has(Type t);

    /**
     * @param name
     *            name of the generator
     * @return true if this repository contains a generator associated with the given name
     */
    boolean has(String name);

    /**
     * @param t
     *            type of the generator
     * @return true if this repository contains a default generator of the given type
     */
    boolean hasDefault(Type t);

    /**
     * @param t
     *            parameterized type of values produced by the synthetic generator
     * @return true if this repository contains a generator which can be synthesized for the given type
     */
    boolean hasSynthetic(Type t);

    /**
     * @param t
     *            type of the values generated by the generator
     * @return the generator which generates values of the given type
     * @throws IllegalArgumentException
     *             if no generator was found
     */
    G get(Type t);

    /**
     * @param name
     *            name of the generator
     * @return the generator associated with the given name
     * @throws IllegalArgumentException
     *             if no generator was found
     */
    G get(String name);

    /**
     * Depending on the generator provider (FJ/java.net.QuickCheck) this method resolves default generators for the
     * given type. This method is usually called as a last resort after trying out all other possibilities.
     * 
     * @param t
     *            type of the values generated by the generator
     * @return a default generator which generates values of the given type
     * @throws IllegalArgumentException
     *             if no generator was found
     */
    G getDefault(Type t);

    /**
     * @param type
     *            of the generator
     * @param components
     *            to be used when synthesizing the generator
     * @return a generator which was synthesized using the given parameters
     */
    G getSynthetic(Type type, List<G> components);
}
