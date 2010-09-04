package lt.dm3.hamcrest.matchers;

import java.lang.reflect.Type;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

class SameTypeMatcher extends TypeSafeMatcher<Type> {

    private final Type type;

    private SameTypeMatcher(Type type) {
        this.type = type;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("type < ").appendText(type.toString());
    }

    @Override
    public boolean matchesSafely(Type other) {
        return type.equals(other);
    }

    public static Matcher<Type> sameTypeAs(Type type) {
        return new SameTypeMatcher(type);
    }
}
