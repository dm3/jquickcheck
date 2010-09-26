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
 * Generator resolution algorithm:
 * <ol>
 * <li>Check if parameter has a defined name (defined through the <tt>G</tt> annotation)
 * <ol>
 * <li>if so: try to get a generator with a matching name from the repository. If succeeded - finish.</li>
 * <li>if not: continue</li>
 * </ol>
 * </li>
 * <li>Check if a generator exists for the exact type of the parameter
 * <ol>
 * <li>if exists: get the generator for the defined type and finish</li>
 * <li>if doesn't exist: continue</li>
 * </ol>
 * </li>
 * <li>Check if <tt>useDefaults</tt> option is true and a default generator exists for the exact type of the parameter
 * <ol>
 * <li>if exists: get the generator for the defined type and finish</li>
 * <li>if doesn't or <tt>useDefaults</tt> is false: continue</li>
 * </ol>
 * </li>
 * <li>Check if <tt>useSynthetics</tt> option is true
 * <ol>
 * <li>if true: try to synthesize a generator for the given type using the same repository and settings as were passed
 * into this property</li>
 * <li>if false: throw {@link QuickCheckException}</li>
 * </ol>
 * </li>
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
                if (repo.has(name)) {
                    gen = repo.get(name);
                    // TODO: if name is specified for a component of a synthetic generator,
                    // allow to create a synthetic one?
                } else {
                    throw new QuickCheckException("Could not find a generator for name: " + name);
                }
            }
        }
        if (gen == null && repo.has(type)) {
            gen = repo.get(type);
        }
        if (gen == null && settings.useDefaults() && repo.hasDefault(type)) {
            gen = repo.getDefault(type);
        }
        if (gen == null && settings.useSynthetics()) {
            gen = repo.getSynthetic(type);
        }
        if (gen == null) {
            throw new QuickCheckException("Could not find a generator for parameter: " + this);
        }
        return gen;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Property of [" + type + "]");
        if (annotations.length > 0) {
            result.append("with @{" + Arrays.toString(annotations) + "}");
        }
        return result.toString();
    }

}
