package lt.dm3.jquickcheck.api;

import java.util.List;

/**
 * Encapsulates the invocation of the property.
 * 
 * @author dm3
 * 
 */
public interface PropertyInvocation<GEN> {

    /**
     * @param param
     * @return true if property is true for the given arguments
     */
    boolean invoke(Object... param);

    /**
     * @return all of the generators used with this property in the order they must appear in the invocation (in the
     *         order they are passed into the test method)
     */
    List<GEN> generators();

}
