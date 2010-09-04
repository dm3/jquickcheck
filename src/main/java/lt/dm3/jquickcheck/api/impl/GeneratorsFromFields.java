package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.internal.Primitives;

public abstract class GeneratorsFromFields<G> implements GeneratorRepository<G> {
    private final Map<String, G> generators = new HashMap<String, G>();

    @SuppressWarnings("unchecked")
    protected GeneratorsFromFields(Iterable<Field> fields, Object context) {
        for (Field field : fields) {
            try {
                generators.put(field.getName(), (G) field.get(context));
            } catch (IllegalArgumentException e) {
                System.err.println(e);
            } catch (IllegalAccessException e) {
                System.err.println(e);
            }
        }
    }

    @Override
    public boolean hasGeneratorFor(String fieldName) {
        return generators.containsKey(fieldName);
    }

    @Override
    public G getGeneratorFor(String fieldName) {
        return generators.get(fieldName);
    }

    @Override
    public boolean hasGeneratorFor(Type t) {
        return findGeneratorFor(t) != null;
    }

    @Override
    public G getGeneratorFor(Type t) {
        G gen = findGeneratorFor(t);
        if (gen == null) {
            throw new IllegalArgumentException("Generator not found!");
        }
        return gen;
    }

    @Override
    public abstract G getDefaultGeneratorFor(Type t);

    private G findGeneratorFor(Type t) {
        for (G g : generators.values()) {
            Type[] interfaces = g.getClass().getGenericInterfaces();
            for (Type i : interfaces) {
                if (i instanceof ParameterizedType) { // i.equals(Generator.class) or Generator.class.isAssignableFrom(i) doesn't work
                    ParameterizedType pType = (ParameterizedType) i;
                    if (isGenerator(pType)) {
                        Type[] args = pType.getActualTypeArguments();
                        if (args.length != 1) {
                            throw new IllegalArgumentException("Cannot determine the type of the generator: " + g
                                    + " for argument of type " + t);
                        }
                        if (suitableFor(t, args[0])) {
                            return g;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param pType
     * @return true if the given type represents a generator
     */
    protected abstract boolean isGenerator(ParameterizedType pType);

    /**
     * @param required
     * @param candidate
     * @return true if the parameter type of the generator is suitable to generate values for the required type
     */
    protected boolean suitableFor(Type required, Type candidate) {
        return Primitives.equalIgnoreWrapping(required, candidate);
    }
}