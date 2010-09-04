package lt.dm3.jquickcheck.api;

/**
 * Resolves the {@link GeneratorRepository} from the given context.
 * <p>
 * This strategy should be stateless and have a no-arg constructor because it's instantiated generically.
 * 
 * @author dm3
 * 
 * @param <GEN>
 *            type of the generator
 */
public interface GeneratorResolutionStrategy<GEN> {

    /**
     * If no {@link GeneratorRepository} can be resolved - property execution will fail.
     * 
     * @param <T>
     *            type of the context
     * @param context
     *            any object
     * @return a {@link GeneratorRepository}
     */
    <T> GeneratorRepository<GEN> resolve(T context);

}
