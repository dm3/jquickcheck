package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.internal.Annotations;

import org.junit.Test;

public class DefaultInvocationSettingsTest {

    @Test
    public void shouldRetainTheNonDefaultMinSuccessfulSettingsOfTheMergeTarget() throws SecurityException,
        NoSuchMethodException {
        Settings mergeTarget = new DefaultInvocationSettings(1, DefaultInvocationSettings.DEFAULT_USE_DEFAULTS);
        Settings other = new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL,
                                                       !DefaultInvocationSettings.DEFAULT_USE_DEFAULTS);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.minSuccessful(), equalTo(mergeTarget.minSuccessful()));
    }

    @Test
    public void shouldRetainTheNonDefaultUseDefaultsSettingsOfTheMergeTarget() throws SecurityException,
        NoSuchMethodException {
        Settings mergeTarget = new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL,
                                                             !DefaultInvocationSettings.DEFAULT_USE_DEFAULTS);
        Settings other = new DefaultInvocationSettings(1, DefaultInvocationSettings.DEFAULT_USE_DEFAULTS);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.useDefaults(), equalTo(mergeTarget.useDefaults()));
    }

    @Test
    public void shouldRetainTheNonDefaultSettingsOfTheOtherSettingsObject() {
        Settings mergeTarget = new DefaultInvocationSettings(1, true);
        Settings other = new DefaultInvocationSettings(10, false);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.minSuccessful(), equalTo(other.minSuccessful()));
    }

    @Test
    public void shouldGetSettingsFromQuickCheckAnnotation() {
        QuickCheck qc = Annotations.newInstance(QuickCheck.class);

        checkDefaults(new DefaultInvocationSettings(qc));
    }

    @Test
    public void shouldGetSettingsFromPropertyAnnotation() {
        Property p = Annotations.newInstance(Property.class);

        checkDefaults(new DefaultInvocationSettings(p));
    }

    private static void checkDefaults(DefaultInvocationSettings settings) {
        assertThat(settings.minSuccessful(),
                   equalTo(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL));
        assertThat(settings.useDefaults(),
                   equalTo(DefaultInvocationSettings.DEFAULT_USE_DEFAULTS));
    }

}
