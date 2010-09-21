package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Type;

public interface TypedGenerator<GEN> {

    Type getType();

    GEN getGenerator();

}
