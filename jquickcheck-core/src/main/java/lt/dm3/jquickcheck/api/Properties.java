package lt.dm3.jquickcheck.api;

/**
 * Static methods to be used inside of properties.
 * 
 * @author dm3
 * 
 */
public abstract class Properties {

    private Properties() {
        // static
    }

    public static void continueIf(boolean shouldBeTrue, Object value) {
        if (!shouldBeTrue) {
            throw new DiscardedValue(value);
        }
    }

    public static void continueIf(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new DiscardedValue();
        }
    }
}
