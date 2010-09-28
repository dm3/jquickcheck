package lt.dm3.jquickcheck.api.impl.resolution;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.api.GeneratorResolutionStep;

public class ResolutionFromMethods<GEN> implements GeneratorResolutionStep<GEN> {

    private final Class<?> generatorClass;

    public ResolutionFromMethods(Class<?> generatorClass) {
        this.generatorClass = generatorClass;
    }

    @Override
    public Iterable<NamedAndTypedGenerator<GEN>> resolveFrom(final Object context) {
        final List<NamedAndTypedGenerator<GEN>> result = new ArrayList<NamedAndTypedGenerator<GEN>>();
        Method[] methods = context.getClass().getDeclaredMethods();
        for (final Method method : methods) {
            if (returnsGenerator(method) && method.getAnnotation(G.class) != null) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        method.setAccessible(true);
                        result.add(new GeneratorFromMethod<GEN>(method, context));
                        return null;
                    }
                });
            }
        }
        return result;
    }

    protected boolean returnsGenerator(Method method) {
        return generatorClass.isAssignableFrom(method.getReturnType());
    }

}
