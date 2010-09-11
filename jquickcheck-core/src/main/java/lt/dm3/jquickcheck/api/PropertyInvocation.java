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
     * Configurable invocation-level settings such as the number of times property has to pass in order to be proven.
     */
    public interface Settings {

        /**
         * @return the number of times property has to pass in order to be proven.
         */
        int minSuccessful();

        /**
         * The primary source of settings in the case of a merge will be the Settings object passed as an argument. This
         * settings object will provide only the settings which remain as defaults in the argument settings.
         * 
         * @param settings
         *            object to be used as a primary source of settings
         * @return new Settings object with properties overwritten by the properties of the given settings object
         */
        Settings mergeWith(Settings settings);

        /**
         * @return true if the quick check runner should use default generators provided by quick check
         */
        boolean useDefaults();
    }

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

    /**
     * @return settings of this invocation
     */
    Settings settings();

}
