package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.GeneratorTypeResolver;

public class TypeFromClassResolver implements GeneratorTypeResolver<Class<?>> {

    @Override
    public Type resolveFrom(Class<?> context) {
        return getGenericTypeFor(context);
    }

    private Type getGenericTypeFor(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        Type[] parameters = clazz.getTypeParameters();
        Type parameter = null;
        if (parameters.length == 0) { // type must be in the interface or a superclass
            Type[] interfaces = clazz.getGenericInterfaces();
            for (Type inter : interfaces) {
                if (inter instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) inter;
                    if (pType.getActualTypeArguments().length == 1) {
                        parameter = pType.getActualTypeArguments()[0];
                    }
                }
            }
            if (parameter == null) {
                parameter = getGenericTypeForType(clazz.getGenericSuperclass());
            }
        } else if (parameters.length == 1) {
            parameter = parameters[0];
        }

        return parameter;
    }

    private Type getGenericTypeForType(Type superclass) {
        if (superclass instanceof ParameterizedType) {
            return ((ParameterizedType) superclass).getActualTypeArguments()[0];
        } else if (superclass instanceof Class) {
            return getGenericTypeFor((Class<?>) superclass);
        }
        return null;
    }
}
