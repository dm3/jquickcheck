package lt.dm3.jquickcheck;

public interface QuickCheckResult {
    public boolean isPassed();

    public boolean isProven();

    public boolean isFalsified();

    public boolean isExhausted();
}
