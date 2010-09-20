package lt.dm3.jquickcheck.api;

/**
 * Represents a java Method containing the actual quickcheck property.
 * 
 * @author dm3
 * 
 * @param <GEN>
 *            type of the generator used to satisfy the arguments to this method
 */
public interface PropertyMethod<GEN> {

    /**
     * Creates the invocation of the underlying java method populated by the values of the generators gathered from the
     * given repository.
     * 
     * @param repo
     *            repository to be used for querying generators
     * @return an invocation of the underlying java method
     */
    PropertyInvocation<GEN> createInvocationWith(GeneratorRepository<GEN> repo);

}
