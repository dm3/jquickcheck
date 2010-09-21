package lt.dm3.jquickcheck.sample;

import java.lang.reflect.Field;

import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.impl.ResolutionFromFieldsOfType;

public class SampleResolutionFromFields extends ResolutionFromFieldsOfType<Generator<?>> {

    @Override
    protected boolean holdsGeneratorInstance(Field field) {
        return Generator.class.isAssignableFrom(field.getType());
    }

    @Override
    protected LookupDefaultByType<Generator<?>> createLookupDefaultByType(Object context) {
        return new SampleLookupDefaultByType();
    }

}
