package lt.dm3.jquickcheck.junit4;

import lt.dm3.jquickcheck.QuickCheckResult;

public class QuickCheckException extends RuntimeException {

    private static final long serialVersionUID = 7856404897568627298L;

    private final QuickCheckResult result;

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
}
