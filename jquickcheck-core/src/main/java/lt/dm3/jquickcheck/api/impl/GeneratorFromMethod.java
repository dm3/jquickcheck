package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.api.impl.resolution.NamedAndTypedGenerator;

public class GeneratorFromMethod<GEN> implements NamedAndTypedGenerator<GEN> {
    private final String name;
    private final Type type;
    private final GEN generator;

    @SuppressWarnings("unchecked")
    public GeneratorFromMethod(Method method, Object context) {
        try {
            this.generator = (GEN) method.invoke(context, (Object[]) null);
        } catch (Exception e) {
            throw new RuntimeException("Could not get a generator from method: " + method + " of object " + context, e);
        }
        this.name = method.getName();
        this.type = TypeResolverRegistry.resolveFrom(method);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public GEN getGenerator() {
        return generator;
    }

    @Override
    public Type getType() {
        return type;
    }

}
