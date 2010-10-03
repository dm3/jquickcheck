package lt.dm3.jquickcheck.api;

public class DiscardedValue extends QuickCheckException {

    private static final long serialVersionUID = 2048028311225540003L;

    public DiscardedValue(String message) {
        super(message);
    }

    public DiscardedValue() {
        super("");
    }

}
