package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.internal.Types;

import com.googlecode.gentyref.GenericTypeReflector;

public class DefaultSynthesizer<G> implements Synthesizer<G> {

    private final Map<Class<?>, Synthesized<G>> synthesizedGenerators = new HashMap<Class<?>, Synthesized<G>>();

    public interface Synthesized<G> {
        List<Class<?>> synthesizedFor();

        G synthesize(List<G> components);
    }

    public abstract static class AbstractSynthesized<G> implements Synthesized<G> {

        protected final Class<?> clazz;

        protected AbstractSynthesized(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public List<Class<?>> synthesizedFor() {
            List<Class<?>> result = new ArrayList<Class<?>>();
            result.add(clazz);
            result.addAll(Types.allParameterizedSuperTypesOf(clazz));
            return result;
        }

        @Override
        public String toString() {
            return String.format("Synthesized for %s and superclasses", clazz);
        }
    }

    public DefaultSynthesizer(Iterable<Synthesized<G>> synthesizeds) {
        for (Synthesized<G> s : synthesizeds) {
            for (Class<?> clazz : s.synthesizedFor()) {
                if (!synthesizedGenerators.containsKey(clazz)) {
                    synthesizedGenerators.put(clazz, s);
                }
            }
        }
    }

    @Override
    public G synthesize(ParameterizedType t, List<G> components) {
        validate(t, components);

        Class<?> clazz = GenericTypeReflector.erase(t);
        if (synthesizedGenerators.containsKey(clazz)) {
            return synthesizedGenerators.get(clazz).synthesize(components);
        }
        throw new QuickCheckException("Could not find a synthesizer for class: " + clazz + " in " + this);
    }

    private void validate(ParameterizedType t, List<G> components) {
        if (t.getActualTypeArguments().length != components.size()) {
            throw new IllegalArgumentException(String.format(
                    "Not enough generators (%s) provided to synthesize type: %s", components, t));
        }
    }

    @Override
    public String toString() {
        return "Synthesizer: " + synthesizedGenerators.toString();
    }

}
