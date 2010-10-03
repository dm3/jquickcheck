package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import lt.dm3.jquickcheck.api.DiscardedValue;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.internal.Primitives;

public class DefaultPropertyInvocation<GEN> implements PropertyInvocation<GEN> {

    private final Object target;
    private final Method method;
    private final Settings settings;
    private final List<GEN> generators;

    public DefaultPropertyInvocation(Object target, Method method, Settings settings, List<GEN> generators) {
        this.target = target;
        this.method = method;
        this.settings = settings;
        this.generators = generators;
    }

    @Override
    public Result invoke(Object... param) {
        Result result = Result.FALSIFIED;
        try {
            Object invocationResult = method.invoke(target, param);
            if (invocationResult != null) {
                if (!Primitives.isBoolean(invocationResult.getClass())) {
                    throw new IllegalArgumentException("Property method " + method + " on " + target
                            + " returns non-boolean!");
                }
                result = Result.from((Boolean) invocationResult);
            } else if (invocationResult == null) {
                // completed normally, property returns void
                result = Result.PROVEN;
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof AssertionError) {
                return Result.FALSIFIED;
            } else if (e.getCause() instanceof DiscardedValue) {
                return Result.DISCARDED;
            }
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public List<GEN> generators() {
        return generators;
    }

    @Override
    public Settings settings() {
        return settings;
    }

}
