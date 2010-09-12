package lt.dm3.jquickcheck.testng;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodFilter;
import lt.dm3.jquickcheck.Property;

public class QuickCheckPropertyFilter implements MethodFilter {

    public boolean isHandled(Method m) {
        return m.getAnnotation(Property.class) != null;
    }

}
