package lt.dm3.jquickcheck.api;

public class QuickCheckException extends RuntimeException {

    private static final long serialVersionUID = 7856404897568627298L;

    // Don't want to make result serializable
    private final transient QuickCheckResult result;

    public QuickCheckException(QuickCheckResult result2) {
        this.result = result2;
    }

    @Override
    public String getMessage() {
        if (result.isExhausted()) {
            return "Exhausted";
        } else if (result.isFalsified()) {
            return "Falsified";
        } else if (result.isPassed()) {
            return "Passed";
        } else if (result.isProven()) {
            return "Proven";
        }
        return "Unknown result!";
    }

    public static QuickCheckException falsified() {
        return new QuickCheckException(new QuickCheckResult() {

            @Override
            public boolean isProven() {
                return false;
            }

            @Override
            public boolean isPassed() {
                return false;
            }

            @Override
            public boolean isFalsified() {
                return true;
            }

            @Override
            public boolean isExhausted() {
                return false;
            }
        });
    }
}
