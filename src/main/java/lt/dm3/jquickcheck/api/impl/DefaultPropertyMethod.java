package lt.dm3.jquickcheck.api.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyMethod;
import lt.dm3.jquickcheck.api.PropertyParameter;

public class DefaultPropertyMethod<GEN> implements PropertyMethod<GEN> {
    private final Method method;
    private final Object target;
    private final List<PropertyParameter<GEN>> parameters;

    public DefaultPropertyMethod(Method method, Object target) {
        this.method = method;
        Type[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();
        List<PropertyParameter<GEN>> parameters = new ArrayList<PropertyParameter<GEN>>(parameterTypes.length);
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters.add(new DefaultPropertyParameter<GEN>(parameterTypes[i], annotations[i]));
        }
        this.parameters = Collections.unmodifiableList(parameters);
        this.target = target;
    }

    @Override
    public PropertyInvocation<GEN> createInvocationWith(GeneratorRepository<GEN> repo) {
        final List<GEN> generators = new ArrayList<GEN>(parameters.size());
        for (PropertyParameter<GEN> param : parameters) {
            generators.add(param.getGeneratorFrom(repo));
        }
        return new PropertyInvocation<GEN>() {
            @Override
            public boolean invoke(Object param) {
                try {
                    return (Boolean) method.invoke(target, param);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public List<GEN> generators() {
                return Collections.unmodifiableList(generators);
            }
        };
    }

}