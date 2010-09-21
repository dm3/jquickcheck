package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;

import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.impl.ResolutionFromFieldsOfType;
import fj.test.Arbitrary;

public class FJGeneratorResolutionStrategy extends ResolutionFromFieldsOfType<Arbitrary<?>> {

    @Override
    protected boolean holdsGeneratorInstance(Field field) {
        return Arbitrary.class.isAssignableFrom(field.getType());
    }

    @Override
    protected LookupDefaultByType<Arbitrary<?>> createLookupDefaultByType(Object context) {
        return new FJLookupDefaultByType();
    }

}
