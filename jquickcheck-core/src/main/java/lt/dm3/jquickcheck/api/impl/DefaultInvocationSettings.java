package lt.dm3.jquickcheck.api.impl;

import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;

public class DefaultInvocationSettings implements Settings {

    public static final int DEFAULT_MIN_SUCCESSFUL = 100;
    public static final boolean DEFAULT_USE_DEFAULTS = false;
    public static final boolean DEFAULT_USE_SYNTHETICS = true;

    private final int minSuccessful;
    private final boolean useDefaults;
    private final boolean useSynthetics;

    public DefaultInvocationSettings(QuickCheck annotation) {
        this(annotation.minSuccessful(), annotation.useDefaults(), annotation.useSynthetics());
    }

    public DefaultInvocationSettings(Property propertyAnnotation) {
        this(propertyAnnotation.minSuccessful(), propertyAnnotation.useDefaults(), propertyAnnotation.useSynthetics());
    }

    DefaultInvocationSettings() {
        this(DEFAULT_MIN_SUCCESSFUL, DEFAULT_USE_DEFAULTS, DEFAULT_USE_SYNTHETICS);
    }

    DefaultInvocationSettings(int minSuccessful, boolean useDefaults, boolean useSynthetics) {
        this.minSuccessful = minSuccessful;
        this.useDefaults = useDefaults;
        this.useSynthetics = useSynthetics;
    }

    @Override
    public boolean useDefaults() {
        return useDefaults;
    }

    @Override
    public boolean useSynthetics() {
        return useSynthetics;
    }

    @Override
    public int minSuccessful() {
        return minSuccessful;
    }

    @Override
    public Settings mergeWith(Settings other) {
        int minSuccessful = other.minSuccessful() == DEFAULT_MIN_SUCCESSFUL ? this.minSuccessful :
                                                                              other.minSuccessful();
        boolean useDefaults = other.useDefaults() == DEFAULT_USE_DEFAULTS ? this.useDefaults : other.useDefaults();
        boolean useSynthetics = other.useSynthetics() == DEFAULT_USE_SYNTHETICS ? this.useSynthetics :
                                                                                  other.useSynthetics();
        return new DefaultInvocationSettings(minSuccessful, useDefaults, useSynthetics);
    }

}
