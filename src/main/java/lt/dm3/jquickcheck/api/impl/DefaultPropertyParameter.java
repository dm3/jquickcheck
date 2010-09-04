package lt.dm3.jquickcheck.api.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyParameter;

class DefaultPropertyParameter<GEN> implements PropertyParameter<GEN> {
    private final Type type;
    private final Annotation[] annotations;

    DefaultPropertyParameter(Type type, Annotation[] annotations) {
        this.type = type;
        this.annotations = annotations;
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
        if (gen == null) {
            if (repo.hasGeneratorFor(type)) {
                gen = repo.getGeneratorFor(type);
            }
        }
        if (gen == null) {
            gen = repo.getDefaultGeneratorFor(type);
        }
        return gen;
    }

}
