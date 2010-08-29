package lt.dm3.jquickcheck.junit.runners;

import java.lang.reflect.Type;

import fj.test.Shrink;

public class ShrinkFactory {

    @SuppressWarnings("unchecked")
    public static <T> Shrink<T> shrinkFor(Type t) {
        if (t.equals(Integer.TYPE)) {
            return (Shrink<T>) Shrink.shrinkInteger;
        } else if (t.equals(Double.TYPE)) {
            return (Shrink<T>) Shrink.shrinkDouble;
        } else if (t.equals(Long.TYPE)) {
            return (Shrink<T>) Shrink.shrinkLong;
        } else if (t.equals(Short.TYPE)) {
            return (Shrink<T>) Shrink.shrinkShort;
        }
        throw new IllegalArgumentException("Unknown primitive type: " + t);
    }

}
