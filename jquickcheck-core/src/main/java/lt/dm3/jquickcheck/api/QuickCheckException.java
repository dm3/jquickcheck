package lt.dm3.jquickcheck.api;

public class QuickCheckException extends RuntimeException {

    private static final long serialVersionUID = 7856404897568627298L;

    private final Throwable cause;
    private final String message;

    public QuickCheckException(String message) {
        this(message, null);
    }

    public QuickCheckException(String message, Throwable t) {
        this.message = message;
        this.cause = t;
    }

    public QuickCheckException(QuickCheckResult result) {
        this.cause = null;
        StringBuilder message = new StringBuilder();
        if (result.isExhausted()) {
            message.append("Exhausted");
        } else if (result.isFalsified()) {
            message.append("Falsified");
        } else if (result.isPassed()) {
            message.append("Passed");
        } else if (result.isProven()) {
            message.append("Proven");
        }
        if (result.exception() != null) {
            message.append("\n").append("Exception: " + result.exception());
        }
        if (!result.arguments().isEmpty()) {
            message.append("\n").append("Arguments: " + result.arguments());
        }
        if (message.length() == 0) {
            this.message = "Unknown result!";
        } else {
            this.message = message.toString();
        }
    }

    @Override
    public String getMessage() {
        return cause == null ? message : message + ", cause: " + cause.getMessage();
    }

}
