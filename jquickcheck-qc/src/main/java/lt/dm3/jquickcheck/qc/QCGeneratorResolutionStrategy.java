package lt.dm3.jquickcheck.qc;

import java.lang.reflect.Field;

import lt.dm3.jquickcheck.api.impl.ResolutionFromFieldsOfType;
import net.java.quickcheck.Generator;

public class QCGeneratorResolutionStrategy extends ResolutionFromFieldsOfType<Generator<?>> {

    @Override
    protected boolean holdsGeneratorInstance(Field field) {
        return Generator.class.isAssignableFrom(field.getType());
    }

}
