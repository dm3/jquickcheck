package lt.dm3.jquickcheck.api.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;

import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.PropertyParameter;
import lt.dm3.jquickcheck.api.QuickCheckException;

class DefaultPropertyParameter<GEN> implements PropertyParameter<GEN> {
    private final Type type;
    private final Annotation[] annotations;
    private final Settings settings;

    DefaultPropertyParameter(Type type, Annotation[] annotations, Settings methodSettings) {
        this.type = type;
        this.annotations = annotations;
        this.settings = methodSettings;
    }

    @Override
    public GEN getGeneratorFrom(GeneratorRepository<GEN> repo) {
        GEN gen = null;
        for (Annotation ann : annotations) {
            if (ann instanceof G) {
                G arbAnnotation = (G) ann;
                String name = arbAnnotation.gen();
                if (repo.hasGeneratorFor(name)) {
                    gen = repo.getGeneratorFor(name);
                }
            }
        }
        if (gen == null && repo.hasGeneratorFor(type)) {
            gen = repo.getGeneratorFor(type);
        }
        if (gen == null && settings.useDefaults()) {
            gen = repo.getDefaultGeneratorFor(type);
        }
        if (gen == null) {
            throw new QuickCheckException("Could not find a generator for parameter: " + this);
        }
        return gen;
    }

    @Override
    public String toString() {
        return "Property of [" + type + "] with @{" + Arrays.toString(annotations) + "}";
    }

}
