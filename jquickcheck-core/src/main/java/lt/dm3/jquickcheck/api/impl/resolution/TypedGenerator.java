package lt.dm3.jquickcheck.api.impl.resolution;

import java.lang.reflect.Type;

public interface TypedGenerator<GEN> {

    Type getType();

    GEN getGenerator();

}
