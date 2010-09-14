package lt.dm3.jquickcheck.api.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;

import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.PropertyMethod;
import lt.dm3.jquickcheck.api.PropertyMethodFactory;

public class DefaultPropertyMethodFactory<GEN> implements PropertyMethodFactory<GEN> {

    private static final class NoArgumentMethod<GEN> implements PropertyMethod<GEN> {
        private final Method method;
        private final Object target;
        private final Settings defaultSettings;

        public NoArgumentMethod(Method method, Object target, Settings defaultSettings) {
            this.method = method;
            this.target = target;
            this.defaultSettings = defaultSettings;
        }

        @Override
        public PropertyInvocation<GEN> createInvocationWith(GeneratorRepository<GEN> repo) {
            return new DefaultPropertyInvocation<GEN>(target, method, defaultSettings, Collections.<GEN> emptyList());
        }

    }

    private final Settings defaultSettings;

    public DefaultPropertyMethodFactory(Settings defaultSettings) {
        this.defaultSettings = defaultSettings;
    }

    @Override
    public PropertyMethod<GEN> createMethod(Method method, Object target) {
        Annotation propertyAnnotation = method.getAnnotation(Property.class);
        Settings settingsToUse = defaultSettings;
        if (propertyAnnotation != null) {
            settingsToUse = settingsToUse.mergeWith(new DefaultInvocationSettings((Property) propertyAnnotation));
        }
        if (method.getParameterTypes().length == 0) {
            return new NoArgumentMethod<GEN>(method, target, settingsToUse);
        }
        return new DefaultPropertyMethod<GEN>(method, target, settingsToUse);
    }

}
