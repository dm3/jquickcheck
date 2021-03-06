package lt.dm3.jquickcheck.api;

import java.lang.reflect.Type;

/**
 * Repository for generators.
 * 
 * @author dm3
 * 
 * @param <G>
 *            type of the generator
 */
public interface GeneratorRepository<G> extends RepositoryContains {

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
     * @return a generator which was synthesized using the given parameters
     */
    G getSynthetic(Type type);
}
