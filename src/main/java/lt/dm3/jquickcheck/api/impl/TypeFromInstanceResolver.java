package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.GeneratorTypeResolver;

public class TypeFromInstanceResolver<T> implements GeneratorTypeResolver<T> {

    @SuppressWarnings("unchecked")
    @Override
    public Type resolveFrom(T context) {
        return getGenericTypeFor((Class<T>) context.getClass());
    }

    private Type getGenericTypeFor(Class<? super T> clazz) {
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
                parameter = getGenericTypeFor(clazz.getGenericSuperclass());
            }
        } else if (parameters.length == 1) {
            parameter = parameters[0];
        }

        return parameter;
    }

    @SuppressWarnings("unchecked")
    private Type getGenericTypeFor(Type superclass) {
        if (superclass instanceof ParameterizedType) {
            return ((ParameterizedType) superclass).getActualTypeArguments()[0];
        } else if (superclass instanceof Class) {
            return getGenericTypeFor((Class<? super T>) superclass);
        }
        return null;
    }
}
