package lt.dm3.hamcrest.matchers;

import java.lang.reflect.Type;

import org.hamcrest.Matcher;

public abstract class AllMatchers {

    private AllMatchers() {
        // static utils
    }

    public static Matcher<Type> sameTypeAs(Type type) {
        return SameTypeMatcher.sameTypeAs(type);
    }
}
