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
    public void shouldRetainTheNonDefaultSettingsOfTheMergeTarget() throws SecurityException, NoSuchMethodException {
        Settings mergeTarget = new DefaultInvocationSettings(1);
        Settings other = new DefaultInvocationSettings(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.minSuccessful(), equalTo(mergeTarget.minSuccessful()));
    }

    @Test
    public void shouldRetainTheNonDefaultSettingsOfTheOtherSettingsObject() {
        Settings mergeTarget = new DefaultInvocationSettings(1);
        Settings other = new DefaultInvocationSettings(10);

        Settings result = mergeTarget.mergeWith(other);

        assertThat(result.minSuccessful(), equalTo(other.minSuccessful()));
    }

    @Test
    public void shouldGetSettingsFromQuickCheckAnnotation() {
        QuickCheck qc = Annotations.newInstance(QuickCheck.class);

        assertThat(new DefaultInvocationSettings(qc).minSuccessful(),
                   equalTo(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL));
    }

    @Test
    public void shouldGetSettingsFromPropertyAnnotation() {
        Property p = Annotations.newInstance(Property.class);

        assertThat(new DefaultInvocationSettings(p).minSuccessful(),
                   equalTo(DefaultInvocationSettings.DEFAULT_MIN_SUCCESSFUL));
    }

}
