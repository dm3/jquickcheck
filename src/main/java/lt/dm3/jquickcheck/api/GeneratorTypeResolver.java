package lt.dm3.jquickcheck.api;

import java.lang.reflect.Type;

public interface GeneratorTypeResolver<T> {

    Type resolveFrom(T context);

}
