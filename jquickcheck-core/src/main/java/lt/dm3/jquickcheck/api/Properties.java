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

    public static void continueIf(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new DiscardedValue();
        }
    }

    public static void discardIf(boolean shouldBeFalse) {
        continueIf(!shouldBeFalse);
    }
}
