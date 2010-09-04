package lt.dm3.jquickcheck.api;

import java.util.List;

/**
 * Adapts the quickcheck engine (FJ/java.net.QuickCheck).
 * <p>
 * The implementation of this adapter should be stateless and have a public no-arg constructor.
 * 
 * @author dm3
 * 
 * @param <GEN>
 *            type of the generator
 */
public interface QuickCheckAdapter<GEN> {

    /**
     * @param generators
     *            in the order they are passed into the test method
     * @param invocation
     *            encapsulates a test method invocation
     * @return Result of performing a quickCheck run with the given generators and the invocation
     */
    QuickCheckResult check(List<GEN> generators, PropertyInvocation invocation);

}
