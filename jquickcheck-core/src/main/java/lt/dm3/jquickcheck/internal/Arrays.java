package lt.dm3.jquickcheck.internal;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public abstract class Arrays {

    private Arrays() {
        // static utils
    }

    public static boolean isArray(Type type) {
        return (type instanceof GenericArrayType) || ((type instanceof Class) && ((Class<?>) type).isArray());
    }

}
