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
import lt.dm3.jquickcheck.internal.Types;

public class DefaultRequestToSynthesize<G> implements RequestToSynthesize<G> {

    private final Settings settings;

    public DefaultRequestToSynthesize(Settings settings) {
        this.settings = settings;
    }

    @Override
    public G synthesize(ParameterizedType t, Synthesizer<G> synth, GeneratorRepository<G> repo) {
        Type[] params = t.getActualTypeArguments();
        List<G> components = new ArrayList<G>(params.length);
        for (Type param : params) {
            if (shouldBeSynthesized(param, repo)) {
                components.add(this.synthesize((ParameterizedType) param, synth, repo));
            } else {
                components.add(getComponentFor(param, repo));
            }
        }
        return synth.synthesize(t, components);
    }

    /**
     * @param type
     * @param repo
     * @return true if a generator for the given type should be synthesized
     */
    private boolean shouldBeSynthesized(Type type, GeneratorRepository<G> repo) {
        return !repo.hasGeneratorFor(type) && Types.hasTypeArguments(type) && type instanceof ParameterizedType;
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
