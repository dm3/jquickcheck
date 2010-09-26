package lt.dm3.jquickcheck.api;

import java.lang.reflect.Type;

public interface RepositoryContains extends LookupContains<Type> {

    /**
     * @param name
     *            name of the generator
     * @return true if this repository contains a generator associated with the given name
     */
    boolean has(String name);

    /**
     * @param t
     *            type of the generator
     * @return true if this repository contains a default generator of the given type
     */
    boolean hasDefault(Type t);

    /**
     * @param t
     *            parameterized type of values produced by the synthetic generator
     * @return true if this repository contains a generator which can be synthesized for the given type
     */
    boolean hasSynthetic(Type t);

}
