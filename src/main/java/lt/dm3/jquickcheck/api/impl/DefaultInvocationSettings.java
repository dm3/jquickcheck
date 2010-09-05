package lt.dm3.jquickcheck.api.impl;

import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;

public class DefaultInvocationSettings implements Settings {

    private final int minSuccessful;

    public DefaultInvocationSettings(QuickCheck annotation) {
        this.minSuccessful = annotation.minSuccessful();
    }

    DefaultInvocationSettings(int minSuccessful) {
        this.minSuccessful = minSuccessful;
    }

    @Override
    public int minSuccessful() {
        return minSuccessful;
    }

}
