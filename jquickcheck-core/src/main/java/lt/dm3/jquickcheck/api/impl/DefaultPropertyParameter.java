package lt.dm3.jquickcheck.api.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;

import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.PropertyParameter;
import lt.dm3.jquickcheck.api.QuickCheckException;

/**
 * Fails to resolve a generator given the property parameters (annotations, type and settings) in these cases:
 * <ol>
 * <li>Annotation for a named generator is specified, but a generator with this name cannot be found in the repository</li>
 * <li>UseDefaults option is turned off in the settings and repository doesn't contain a generator for the given
 * name/type</li>
 * <li>UseDefaults is turned on and repository doesn't contain a generator for the given name/type or a default
 * generator for the given type</li>
 * </ol>
 * 
 * @author dm3
 * 
 * @param <GEN>
 */
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
                    // TODO: if name is specified for a component of a synthetic generator,
                    // allow to create a synthetic one?
                } else {
                    throw new QuickCheckException("Could not find a generator for name: " + name);
                }
            }
        }
        if (gen == null && repo.hasGeneratorFor(type)) {
            gen = repo.getGeneratorFor(type);
        }
        if (gen == null && settings.useDefaults() && repo.hasDefaultGeneratorFor(type)) {
            gen = repo.getDefaultGeneratorFor(type);
        }
        if (gen == null && settings.useSynthetics()) {
            gen = repo.getSyntheticGeneratorFor(new DefaultRequestToSynthesize<GEN>(type, settings));
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
