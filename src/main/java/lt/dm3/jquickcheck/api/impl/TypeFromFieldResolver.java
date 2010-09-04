package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.GeneratorTypeResolver;

public class TypeFromFieldResolver implements GeneratorTypeResolver<Field> {

    @Override
    public Type resolveFrom(Field context) {
        final Type type = context.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getActualTypeArguments().length == 1) {
                return pType.getActualTypeArguments()[0];
            }
        }
        return null;
    }

}
