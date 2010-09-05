package lt.dm3.jquickcheck.api.impl;

import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;

public class DefaultInvocationSettings implements Settings {

    // TODO: default minSuccessful is also repeated in @Property and @QuickCheck
    private static final int DEFAULT_MIN_SUCCESSFUL = 100;

    private final int minSuccessful;

    public DefaultInvocationSettings(QuickCheck annotation) {
        this.minSuccessful = annotation.minSuccessful();
    }

    public DefaultInvocationSettings(Property propertyAnnotation) {
        this.minSuccessful = propertyAnnotation.minSuccessful();
    }

    DefaultInvocationSettings(int minSuccessful) {
        this.minSuccessful = minSuccessful;
    }

    @Override
    public int minSuccessful() {
        return minSuccessful;
    }

    @Override
    public Settings mergeWith(Settings other) {
        if (other.minSuccessful() == DEFAULT_MIN_SUCCESSFUL) {
            return this;
        }
        return new DefaultInvocationSettings(other.minSuccessful());
    }

}
