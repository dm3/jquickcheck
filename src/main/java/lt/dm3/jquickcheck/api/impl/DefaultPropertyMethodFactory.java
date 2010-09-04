package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyMethod;
import lt.dm3.jquickcheck.api.PropertyMethodFactory;

public class DefaultPropertyMethodFactory<GEN> implements PropertyMethodFactory<GEN> {

    private static final class NoArgumentMethod<GEN> implements PropertyMethod<GEN> {
        private final Method method;
        private final Object target;

        NoArgumentMethod(Method method, Object target) {
            this.method = method;
            this.target = target;
        }

        @Override
        public PropertyInvocation<GEN> createInvocationWith(GeneratorRepository<GEN> repo) {
            return new PropertyInvocation<GEN>() {
                @Override
                public boolean invoke(Object param) {
                    try {
                        return (Boolean) method.invoke(target, (Object[]) null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public List<GEN> generators() {
                    return Collections.emptyList();
                }
            };
        }

    }

    @Override
    public PropertyMethod<GEN> createMethod(Method method, Object target) {
        if (method.getParameterTypes().length == 0) {
            return new NoArgumentMethod<GEN>(method, target);
        }
        return new DefaultPropertyMethod<GEN>(method, target);
    }

}
