package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.GeneratorTypeResolver;

public class TypeFromMethodResolver implements GeneratorTypeResolver<Method> {

    @Override
    public Type resolveFrom(Method context) {
        final Type type = context.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getActualTypeArguments().length == 1) {
                return pType.getActualTypeArguments()[0];
            }
        }
        return null;
    }

}
