package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.RequestToSynthesize;
import lt.dm3.jquickcheck.internal.Arrays;
import lt.dm3.jquickcheck.internal.Types;

import com.googlecode.gentyref.GenericTypeReflector;

public class DefaultRequestToSynthesize<G> implements RequestToSynthesize<G> {

    private final Type type;
    private final Settings settings;

    public DefaultRequestToSynthesize(Type type, Settings settings) {
        if (!(type instanceof ParameterizedType || Arrays.isArray(type))) {
            throw new IllegalArgumentException(
                    "Can only synthesize parameterized or array types. Tried to synthesize: " + type);
        }
        this.type = type;
        this.settings = settings;
    }

    @Override
    public G synthesize(GeneratorRepository<G> repo) {
        if (type instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) type).getActualTypeArguments();
            List<G> components = new ArrayList<G>(params.length);
            for (Type param : params) {
                if (shouldBeSynthesized(param, repo)) {
                    components.add(new DefaultRequestToSynthesize<G>(param, settings).synthesize(repo));
                } else {
                    components.add(getComponentFor(param, repo));
                }
            }
            return repo.getSyntheticGeneratorFor(type, components);
        } else if (Arrays.isArray(type)) {
            Type componentType = GenericTypeReflector.getArrayComponentType(type);
            List<G> components = new ArrayList<G>(1);
            // TODO: duplication with the previous if block
            if (shouldBeSynthesized(componentType, repo)) {
                components.add(new DefaultRequestToSynthesize<G>(componentType, settings).synthesize(repo));
            } else {
                components.add(getComponentFor(componentType, repo));
            }
            return repo.getSyntheticGeneratorFor(type, components);
        }
        throw new IllegalStateException("Impossible by preconditions in constructor!");
    }

    /**
     * @param type
     * @param repo
     * @return true if a generator for the given type should be synthesized
     */
    private boolean shouldBeSynthesized(Type type, GeneratorRepository<G> repo) {
        return !repo.has(type) && (Types.hasTypeArguments(type) && type instanceof ParameterizedType)
                || Arrays.isArray(type);
    }

    protected G getComponentFor(Type type, GeneratorRepository<G> repo) {
        G result = null;
        if (settings.useDefaults() && !repo.has(type) && repo.hasDefault(type)) {
            result = repo.getDefault(type);
        } else if (repo.has(type)) {
            result = repo.get(type);
        }
        if (result == null) {
            throw new QuickCheckException("Could not synthesize. No generator exists for type: " + type);
        }
        return result;
    }

}
