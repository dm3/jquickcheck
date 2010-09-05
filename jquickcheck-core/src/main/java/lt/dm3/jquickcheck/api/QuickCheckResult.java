package lt.dm3.jquickcheck.api;

import java.util.List;

/**
 * Result of passing quickcheck over one property.
 * <p>
 * Interface adapted from FJ.
 * 
 * @author dm3
 * 
 */
public interface QuickCheckResult {

    boolean isPassed();

    boolean isProven();

    boolean isFalsified();

    boolean isExhausted();

    Throwable exception();

    List<?> arguments();
}
