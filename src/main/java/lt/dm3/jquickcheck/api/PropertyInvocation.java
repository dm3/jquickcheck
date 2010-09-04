package lt.dm3.jquickcheck.api;

/**
 * Encapsulates the invocation of the property.
 * 
 * @author dm3
 * 
 */
public interface PropertyInvocation {

    /**
     * @param param
     * @return true if property is true for the given arguments
     */
    boolean invoke(Object param);

}
