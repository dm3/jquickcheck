package lt.dm3.jquickcheck.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.gentyref.GenericTypeReflector;

/**
 * Static utilities for type magic.
 * 
 * @author dm3
 * 
 */
public class Types {

    private Types() {
        // static utils
    }

    public static List<Class<?>> allParameterizedSuperTypesOf(Type t) {
        List<Class<?>> all = allSuperTypesOf(t);
        List<Class<?>> result = new ArrayList<Class<?>>(all.size());
        for (Class<?> c : all) {
            if (c.getTypeParameters().length > 0) {
                result.add(c);
            }
        }
        return result;
    }

    public static List<Class<?>> allSuperTypesOf(Type t) {
        Set<Type> intermediate = new LinkedHashSet<Type>();
        if (t instanceof Class) {
            Class<?> c = (Class<?>) t;
            Type superclass = c.getGenericSuperclass();
            if (superclass != null) {
                intermediate.addAll(allSuperTypesOf(superclass));
                intermediate.add(superclass);
            }
            Type[] interfaces = c.getGenericInterfaces();
            for (Type interf : interfaces) {
                intermediate.addAll(allSuperTypesOf(interf));
                intermediate.add(interf);
            }
        } else if (t instanceof ParameterizedType) {
            Type raw = ((ParameterizedType) t).getRawType();
            intermediate.addAll(allSuperTypesOf(raw));
            intermediate.add(raw);
        }
        List<Class<?>> result = new ArrayList<Class<?>>(intermediate.size());
        for (Type type : intermediate) {
            result.add(GenericTypeReflector.erase(type));
        }
        return result;
    }

    public static boolean hasTypeArguments(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return pType.getActualTypeArguments().length > 0;
        } else if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            return clazz.getTypeParameters().length > 0;
        }
        return false;
    }
}
