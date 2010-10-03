package lt.dm3.jquickcheck.api;

public class DiscardedValue extends QuickCheckException {

    private static final long serialVersionUID = 2048028311225540003L;

    public DiscardedValue(String message) {
        super(message);
    }

    public DiscardedValue(String message, Object value) {
        super(message + ", Discarded: " + value);
    }

    public DiscardedValue(Object value) {
        super("Discarded: " + value);
    }

    public DiscardedValue() {
        super("");
    }

}
