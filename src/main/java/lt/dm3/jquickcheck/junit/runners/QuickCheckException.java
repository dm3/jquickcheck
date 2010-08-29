package lt.dm3.jquickcheck.junit.runners;

import fj.test.CheckResult;

public class QuickCheckException extends RuntimeException {

    private static final long serialVersionUID = 7856404897568627298L;

    private final CheckResult result;

    public QuickCheckException(CheckResult result) {
        this.result = result;
    }

    @Override
    public String getMessage() {
        return CheckResult.summary.showS(result);
    }
}
