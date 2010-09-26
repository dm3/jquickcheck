package lt.dm3.jquickcheck.api.impl.resolution;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.impl.resolution.ImplicitGeneratorGraph.Node;
import lt.dm3.jquickcheck.internal.Primitives;

public abstract class ResolutionOfImplicits<GEN> {

    private static final class GeneratorFromNode<GEN> implements NamedAndTypedGenerator<GEN> {
        private final String name;
        private final Type type;
        private final boolean isDefault;
        private final GEN generator;

        GeneratorFromNode(Node n, GEN gen) {
            this.name = n.getName();
            this.type = n.getType();
            this.generator = gen;
            this.isDefault = n.isDefault();
        }

        public GEN getGenerator() {
            return generator;
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        public boolean isDefault() {
            return isDefault;
        }

    }

    private final Class<?> generatorClass;

    public ResolutionOfImplicits(Class<?> generatorClass) {
        this.generatorClass = generatorClass;
    }

    private static class ModifiableRepository<GEN> implements GeneratorRepository<GEN> {
        private final List<NamedAndTypedGenerator<GEN>> additional = new ArrayList<NamedAndTypedGenerator<GEN>>();
        private final GeneratorRepository<GEN> underlying;

        ModifiableRepository(GeneratorRepository<GEN> underlying) {
            this.underlying = underlying;
        }

        void add(NamedAndTypedGenerator<GEN> generator) {
            additional.add(generator);
        }

        public boolean has(Type t) {
            for (NamedAndTypedGenerator<GEN> gen : additional) {
                if (Primitives.equalIgnoreWrapping(gen.getType(), t)) {
                    return true;
                }
            }
            return underlying.has(t);
        }

        public boolean has(String name) {
            for (NamedAndTypedGenerator<GEN> gen : additional) {
                if (gen.getName().equals(name)) {
                    return true;
                }
            }
            return underlying.has(name);
        }

        public boolean hasDefault(Type t) {
            return underlying.hasDefault(t);
        }

        public GEN get(Type t) {
            for (NamedAndTypedGenerator<GEN> gen : additional) {
                if (Primitives.equalIgnoreWrapping(gen.getType(), t)) {
                    return gen.getGenerator();
                }
            }
            return underlying.get(t);
        }

        public GEN get(String name) {
            for (NamedAndTypedGenerator<GEN> gen : additional) {
                if (gen.getName().equals(name)) {
                    return gen.getGenerator();
                }
            }
            return underlying.get(name);
        }

        public GEN getDefault(Type t) {
            return underlying.getDefault(t);
        }

        public boolean hasSynthetic(Type t) {
            return underlying.hasSynthetic(t);
        }

        public GEN getSynthetic(Type type) {
            return underlying.getSynthetic(type);
        }

    }

    public Iterable<NamedAndTypedGenerator<GEN>> resolveFrom(Object context, GeneratorRepository<GEN> repo) {
        List<Node> allImplicits = new ArrayList<Node>();
        for (Method method : context.getClass().getDeclaredMethods()) {
            if (isImplicitGenerator(method)) {
                allImplicits.add(new Node(method));
            }
        }
        List<Node> satisfied = new ImplicitGeneratorGraph(allImplicits).satisfy(repo);
        ModifiableRepository<GEN> modifiableRepo = new ModifiableRepository<GEN>(repo);
        for (Node sat : satisfied) {
            // each node might depend on the predecessor, that's why we add the predecessors to the same exact
            // repository we're passing to the following nodes
            modifiableRepo.add(new GeneratorFromNode<GEN>(sat, createImplicitGenerator(context, sat,
                    modifiableRepo)));
        }
        return modifiableRepo.additional;
    }

    private GEN createImplicitGenerator(Object context, Node n, GeneratorRepository<GEN> repo) {
        Type[] params = n.getMethod().getGenericParameterTypes();
        List<GEN> components = new ArrayList<GEN>(params.length);
        for (Type t : params) {
            if (repo.has(t)) {
                components.add(repo.get(t));
            } else if (n.isDefault() && repo.hasDefault(t)) {
                components.add(repo.getDefault(t));
            } else {
                components.add(repo.getSynthetic(t));
            }
        }
        return createImplicitGenerator(context, n.getMethod(), components);
    }

    protected abstract GEN createImplicitGenerator(Object context, Method method, List<GEN> components);

    protected boolean returnsGenerator(Method method) {
        return generatorClass.isAssignableFrom(method.getReturnType());
    }

    private boolean isImplicitGenerator(Method method) {
        return method.getReturnType() != null && !returnsGenerator(method)
                && method.getAnnotation(G.class) != null &&
                method.getAnnotation(Property.class) == null;
    }

}
