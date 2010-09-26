package lt.dm3.jquickcheck.api;

public interface LookupContains<By> {

    /**
     * Query to find out if this Lookup contains at least one item matching the given key.
     * 
     * @param by
     *            key
     * @return true if this Lookup contains at least one item matching the given key
     */
    boolean has(By by);
}
