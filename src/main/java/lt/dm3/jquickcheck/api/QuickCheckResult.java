package lt.dm3.jquickcheck.api;

/**
 * Result of passing quickcheck over one property.
 * <p>
 * Interface adapted from FJ.
 * 
 * @author dm3
 * 
 */
public interface QuickCheckResult {
    public boolean isPassed();

    public boolean isProven();

    public boolean isFalsified();

    public boolean isExhausted();
}
