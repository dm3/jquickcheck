package lt.dm3.jquickcheck.api.impl;

import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;

public class DefaultInvocationSettings implements Settings {

    public static final int DEFAULT_MIN_SUCCESSFUL = 100;
    public static final boolean DEFAULT_USE_DEFAULTS = false;

    private final int minSuccessful;
    private final boolean useDefaults;

    public DefaultInvocationSettings(QuickCheck annotation) {
        this(annotation.minSuccessful(), annotation.useDefaults());
    }

    public DefaultInvocationSettings(Property propertyAnnotation) {
        this(propertyAnnotation.minSuccessful(), propertyAnnotation.useDefaults());
    }

    DefaultInvocationSettings() {
        this(DEFAULT_MIN_SUCCESSFUL, DEFAULT_USE_DEFAULTS);
    }

    DefaultInvocationSettings(int minSuccessful, boolean useDefaults) {
        this.minSuccessful = minSuccessful;
        this.useDefaults = useDefaults;
    }

    @Override
    public boolean useDefaults() {
        return useDefaults;
    }

    @Override
    public int minSuccessful() {
        return minSuccessful;
    }

    @Override
    public Settings mergeWith(Settings other) {
        int minSuccessful = other.minSuccessful() == DEFAULT_MIN_SUCCESSFUL ? this.minSuccessful : other
                .minSuccessful();
        boolean useDefaults = other.useDefaults() == DEFAULT_USE_DEFAULTS ? this.useDefaults : other.useDefaults();
        return new DefaultInvocationSettings(minSuccessful, useDefaults);
    }

}
