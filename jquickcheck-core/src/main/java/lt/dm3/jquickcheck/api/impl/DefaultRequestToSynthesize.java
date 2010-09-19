package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.RequestToSynthesize;
import lt.dm3.jquickcheck.api.Synthesizer;
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
    public G synthesize(Synthesizer<G> synth, GeneratorRepository<G> repo) {
        if (type instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) type).getActualTypeArguments();
            List<G> components = new ArrayList<G>(params.length);
            for (Type param : params) {
                if (shouldBeSynthesized(param, repo)) {
                    components.add(new DefaultRequestToSynthesize<G>(param, settings).synthesize(synth, repo));
                } else {
                    components.add(getComponentFor(param, repo));
                }
            }
            return synth.synthesize(type, components);
        } else if (Arrays.isArray(type)) {
            Type componentType = GenericTypeReflector.getArrayComponentType(type);
            List<G> components = new ArrayList<G>(1);
            // TODO: duplication with the previous if block
            if (shouldBeSynthesized(componentType, repo)) {
                components.add(new DefaultRequestToSynthesize<G>(componentType, settings).synthesize(synth, repo));
            } else {
                components.add(getComponentFor(componentType, repo));
            }
            return synth.synthesize(type, components);
        }
        throw new IllegalStateException("Impossible by preconditions in constructor!");
    }

    /**
     * @param type
     * @param repo
     * @return true if a generator for the given type should be synthesized
     */
    private boolean shouldBeSynthesized(Type type, GeneratorRepository<G> repo) {
        return !repo.hasGeneratorFor(type) && (Types.hasTypeArguments(type) && type instanceof ParameterizedType)
                || Arrays.isArray(type);
    }

    protected G getComponentFor(Type type, GeneratorRepository<G> repo) {
        G result = null;
        if (settings.useDefaults() && !repo.hasGeneratorFor(type)) {
            result = repo.getDefaultGeneratorFor(type);
        } else {
            result = repo.getGeneratorFor(type);
        }
        if (result == null) {
            throw new QuickCheckException("Could not synthesize. No generator exists for type: " + type);
        }
        return result;
    }

}
