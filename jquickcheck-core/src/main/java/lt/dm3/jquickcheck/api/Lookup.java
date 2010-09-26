package lt.dm3.jquickcheck.api;

import java.util.Set;

/**
 * Lookup which looks up.
 * 
 * @author dm3
 * 
 * @param <By>
 *            type of keys
 * @param <To>
 *            type of values
 */
public interface Lookup<By, To> extends LookupContains<By> {

    /**
     * Get a unique value associated with the given key.
     * 
     * @param by
     *            key
     * @return The value associated with the given key
     * @throws IllegalArgumentException
     *             if lookup contains several values associated with the given key
     */
    To get(By by);

    /**
     * @param by
     *            key
     * @return set of values associated with the given key. Returns empty set if no values were found.
     */
    Set<To> getAll(By by);

}
