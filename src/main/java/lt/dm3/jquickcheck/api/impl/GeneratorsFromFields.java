package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.internal.Primitives;

public abstract class GeneratorsFromFields<G> implements GeneratorRepository<G> {
    private final Map<String, G> nameToGenerator = new HashMap<String, G>();
    private final Map<Type, G> typeToGenerator = new HashMap<Type, G>();

    protected GeneratorsFromFields(Iterable<HasGenerator<G>> generators, Object context) {
        for (HasGenerator<G> gen : generators) {
            nameToGenerator.put(gen.getName(), gen.getGenerator());
            if (gen.getType() != null) {
                if (Primitives.isPrimitiveOrWrapper(gen.getType())) {
                    typeToGenerator.put(Primitives.oppositeOf(gen.getType()), gen.getGenerator());
                }
                typeToGenerator.put(gen.getType(), gen.getGenerator());
            }
        }
    }

    @Override
    public boolean hasGeneratorFor(String fieldName) {
        return nameToGenerator.containsKey(fieldName);
    }

    @Override
    public G getGeneratorFor(String fieldName) {
        return nameToGenerator.get(fieldName);
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
        if (typeToGenerator.containsKey(t)) {
            return typeToGenerator.get(t);
        }
        for (G g : nameToGenerator.values()) {
            Type generatorType = getGeneratorTypeFor(g);
            if (suitableFor(t, generatorType)) {
                return g;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected Type getGeneratorTypeFor(G object) {
        Type result = getGenericTypeFor((Class<G>) object.getClass());
        if (result == null) {
            throw impossibleToResolveGeneratorFor(object.getClass());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Type getGenericTypeFor(Class<? super G> clazz) {
        if (clazz == null) {
            return null;
        }

        Type[] parameters = clazz.getTypeParameters();
        Type parameter = null;
        if (parameters.length == 0) { // type must be in the interface or a superclass
            Type[] interfaces = clazz.getGenericInterfaces();
            for (Type inter : interfaces) {
                if (inter instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) inter;
                    if (pType.getActualTypeArguments().length == 1) {
                        parameter = pType.getActualTypeArguments()[0];
                    }
                }
            }
            if (parameter == null) {
                parameter = getGenericTypeFor((Class<? super G>) clazz.getGenericSuperclass());
            }
        } else if (parameters.length == 1) {
            parameter = parameters[0];
        }

        return parameter;
    }

    private IllegalArgumentException impossibleToResolveGeneratorFor(Class<?> clazz) {
        return new IllegalArgumentException(String.format("Class %s must have exactly one type parameter or "
                + "have a super class or interface with exactly one type parameter!", clazz.getName()));
    }

    /**
     * @param required
     * @param candidate
     * @return true if the parameter type of the generator is suitable to generate values for the required type
     */
    protected boolean suitableFor(Type required, Type candidate) {
        return Primitives.equalIgnoreWrapping(required, candidate);
    }
}