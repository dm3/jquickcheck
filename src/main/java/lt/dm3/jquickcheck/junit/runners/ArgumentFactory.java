package lt.dm3.jquickcheck.junit.runners;

import java.lang.reflect.Type;

import fj.test.Arbitrary;

public class ArgumentFactory {

    @SuppressWarnings({ "rawtypes" })
    public static Arbitrary argumentFor(Type t) {
        if (t.equals(Integer.TYPE)) {
            return Arbitrary.arbInteger;
        } else if (t.equals(Double.TYPE)) {
            return Arbitrary.arbDouble;
        } else if (t.equals(Long.TYPE)) {
            return Arbitrary.arbLong;
        } else if (t.equals(Short.TYPE)) {
            return Arbitrary.arbShort;
        }
        throw new IllegalArgumentException("Unknown primitive type: " + t);
    }

}
