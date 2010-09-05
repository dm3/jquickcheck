package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;

import org.junit.Test;

public class DefaultInvocationSettingsTest {

    @Test
    public void shouldRetainTheNonDefaultSettingsOfTheMergeTarget() throws SecurityException, NoSuchMethodException {
        Settings mergeTarget = new DefaultInvocationSettings(1);
        Settings other = new DefaultInvocationSettings((Integer) Property.class.getMethod("minSuccessful")
                .getDefaultValue());

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
}
