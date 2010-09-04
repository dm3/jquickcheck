package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.GeneratorTypeResolver;

public class TypeResolverRegistry {

    private static final GeneratorTypeResolver<Field> fieldResolver = new TypeFromFieldResolver();
    private static final GeneratorTypeResolver<Class<?>> classResolver = new TypeFromClassResolver();

    public static Type resolveFrom(Object context) {
        if (context instanceof Field) {
            return fieldResolver.resolveFrom((Field) context);
        }
        return classResolver.resolveFrom(context.getClass());
    }

}
