package lt.dm3.jquickcheck.junit.runners;

import java.lang.reflect.Type;

import junit.framework.Assert;

import org.junit.runners.model.FrameworkMethod;

public class ArgumentProvider {

    private final FrameworkMethod method;

    public ArgumentProvider(FrameworkMethod method) {
        Assert.assertTrue(method.getMethod().getParameterTypes().length > 0);

        this.method = method;
    }

    public Object[] generateArguments() {
        Type[] argumentTypes = method.getMethod().getGenericParameterTypes();
        Object[] result = new Object[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            Type t = argumentTypes[i];
            result[i] = ArgumentFactory.argumentFor(t);
        }
        return result;
    }
}
