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
    public void shouldLeaveTheDefaultSettingsIfMergedWithDefault() throws SecurityException,
        NoSuchMethodException {
        Settings mergeTarget = new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL,
                                                             DefaultInvocationSettings.DEFAULT_USE_DEFAULTS,
                                                             DefaultInvocationSettings.DEFAULT_USE_SYNTHETICS);
        Settings other = new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL,
                                                             DefaultInvocationSettings.DEFAULT_USE_DEFAULTS,
                                                             DefaultInvocationSettings.DEFAULT_USE_SYNTHETICS);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.minSuccessful(), equalTo(mergeTarget.minSuccessful()));
        assertThat(result.useDefaults(), equalTo(mergeTarget.useDefaults()));
        assertThat(result.useSynthetics(), equalTo(mergeTarget.useSynthetics()));
    }

    @Test
    public void shouldRetainTheNonDefaultMinSuccessfulSettingsOfTheMergeTarget() throws SecurityException,
        NoSuchMethodException {
        Settings mergeTarget = new DefaultInvocationSettings(1, true, false);
        Settings other = new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true, false);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.minSuccessful(), equalTo(mergeTarget.minSuccessful()));
    }

    @Test
    public void shouldRetainTheNonDefaultUseDefaultsSettingsOfTheMergeTarget() throws SecurityException,
        NoSuchMethodException {
        Settings mergeTarget = new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL,
                                                             !DefaultInvocationSettings.DEFAULT_USE_DEFAULTS, false);
        Settings other = new DefaultInvocationSettings(1, DefaultInvocationSettings.DEFAULT_USE_DEFAULTS, false);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.useDefaults(), equalTo(mergeTarget.useDefaults()));
    }

    @Test
    public void shouldRetainTheNonDefaultUseSyntheticsSettingsOfTheMergeTarget() throws SecurityException,
        NoSuchMethodException {
        Settings mergeTarget = new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL, true,
                                                             !DefaultInvocationSettings.DEFAULT_USE_SYNTHETICS);
        Settings other = new DefaultInvocationSettings(1, true, DefaultInvocationSettings.DEFAULT_USE_SYNTHETICS);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.useSynthetics(), equalTo(mergeTarget.useSynthetics()));
    }

    @Test
    public void shouldRetainTheNonDefaultSettingsOfTheOtherSettingsObject_MinSuccessful() {
        Settings mergeTarget = new DefaultInvocationSettings(1, true, false);
        Settings other = new DefaultInvocationSettings(10, false, false);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.minSuccessful(), equalTo(other.minSuccessful()));
    }

    @Test
    public void shouldRetainTheNonDefaultSettingsOfTheOtherSettingsObject_UseSynthetics() {
        Settings mergeTarget = new DefaultInvocationSettings(1, true, DefaultInvocationSettings.DEFAULT_USE_SYNTHETICS);
        Settings other = new DefaultInvocationSettings(10, false, !DefaultInvocationSettings.DEFAULT_USE_SYNTHETICS);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.useSynthetics(), equalTo(other.useSynthetics()));
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
        assertThat(settings.useSynthetics(),
                   equalTo(DefaultInvocationSettings.DEFAULT_USE_SYNTHETICS));
    }

}
