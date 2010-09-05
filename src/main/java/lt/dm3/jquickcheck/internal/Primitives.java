package lt.dm3.jquickcheck.internal;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Initialization blatantly copied from Guava-primitives (Primitives.java). Sad thing Guava Primitives operate on
 * classes (not on {@link Type}).
 * 
 * @author dm3
 * 
 */
public abstract class Primitives {

    private Primitives() {
        // static utils
    }

    /** A map from primitive types to their corresponding wrapper types. */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;

    /** A map from wrapper types to their corresponding primitive types. */
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;

    static {
        Map<Class<?>, Class<?>> primToWrap = new HashMap<Class<?>, Class<?>>(16);
        Map<Class<?>, Class<?>> wrapToPrim = new HashMap<Class<?>, Class<?>>(16);

        add(primToWrap, wrapToPrim, boolean.class, Boolean.class);
        add(primToWrap, wrapToPrim, byte.class, Byte.class);
        add(primToWrap, wrapToPrim, char.class, Character.class);
        add(primToWrap, wrapToPrim, double.class, Double.class);
        add(primToWrap, wrapToPrim, float.class, Float.class);
        add(primToWrap, wrapToPrim, int.class, Integer.class);
        add(primToWrap, wrapToPrim, long.class, Long.class);
        add(primToWrap, wrapToPrim, short.class, Short.class);
        add(primToWrap, wrapToPrim, void.class, Void.class);

        PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(primToWrap);
        WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(wrapToPrim);
    }

    private static void add(Map<Class<?>, Class<?>> forward, Map<Class<?>, Class<?>> backward, Class<?> key,
                            Class<?> value) {
        forward.put(key, value);
        backward.put(value, key);
    }

    public static boolean equalIgnoreWrapping(Type a, Type b) {
        if (a.equals(b)) {
            return true;
        } else if (PRIMITIVE_TO_WRAPPER_TYPE.containsKey(a)) {
            return PRIMITIVE_TO_WRAPPER_TYPE.get(a).equals(b);
        } else if (WRAPPER_TO_PRIMITIVE_TYPE.containsKey(a)) {
            return WRAPPER_TO_PRIMITIVE_TYPE.get(a).equals(b);
        }
        return false;
    }

    public static boolean isPrimitiveOrWrapper(Type t) {
        return isPrimitive(t) || WRAPPER_TO_PRIMITIVE_TYPE.containsKey(t);
    }

    public static Type oppositeOf(Type t) {
        if (!isPrimitiveOrWrapper(t)) {
            throw new IllegalArgumentException("Expected a primitive or a primitive wrapper. Got: " + t);
        }
        if (isPrimitive(t)) {
            return PRIMITIVE_TO_WRAPPER_TYPE.get(t);
        }
        return WRAPPER_TO_PRIMITIVE_TYPE.get(t);
    }

    private static boolean isPrimitive(Type t) {
        return PRIMITIVE_TO_WRAPPER_TYPE.containsKey(t);
    }

    public static boolean isInteger(Type t) {
        return t.equals(Integer.class) || isPrimitive(t) && PRIMITIVE_TO_WRAPPER_TYPE.get(t).equals(Integer.class);
    }
}
