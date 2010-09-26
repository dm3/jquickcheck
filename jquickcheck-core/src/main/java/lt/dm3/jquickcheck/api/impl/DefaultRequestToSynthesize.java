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
import lt.dm3.jquickcheck.api.impl.DefaultRequestToSynthesize.TypeTree.MakeData;
import lt.dm3.jquickcheck.api.impl.DefaultRequestToSynthesize.TypeTree.Visitor;
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

    public static final class TypeTree<T> {
        private final List<TypeTree<T>> children = new ArrayList<TypeTree<T>>();
        private final Type type;
        private T data;

        public TypeTree(Type type) {
            this.type = type;
        }

        public TypeTree(Type type, T data) {
            this.type = type;
            this.data = data;
        }

        public void addChild(TypeTree<T> child) {
            this.children.add(child);
        }

        @Override
        public String toString() {
            return "Type: " + type + ", data: " + data + ", children: \n" + children;
        }

        public void accept(Visitor<T> v) {
            for (TypeTree<T> child : children) {
                child.accept(v);
            }
            this.data = v.visit(type, children);
        }

        public interface Visitor<T> {
            T visit(Type t);

            T visit(Type t, List<TypeTree<T>> children);
        }

        public interface MakeData<T> {
            T make(Type t);
        }
    }

    private static class CanConstructVisitor implements Visitor<Boolean> {
        private final GeneratorRepository<?> repo;

        public CanConstructVisitor(GeneratorRepository<?> repo) {
            this.repo = repo;
        }

        @Override
        public Boolean visit(Type t) {
            return repo.has(t);
        }

        @Override
        public Boolean visit(Type t, List<TypeTree<Boolean>> children) {
            if (!repo.has(t)) {
                for (TypeTree<Boolean> child : children) {
                    if (!child.data) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private static class CreateGeneratorsVisitor<GEN> implements Visitor<GEN> {
        private final GeneratorRepository<GEN> repo;
        private final Synthesizer<GEN> synth;

        public CreateGeneratorsVisitor(GeneratorRepository<GEN> repo, Synthesizer<GEN> synth) {
            this.repo = repo;
            this.synth = synth;
        }

        @Override
        public GEN visit(Type t) {
            return repo.get(t);
        }

        @Override
        public GEN visit(Type t, List<TypeTree<GEN>> children) {
            if (!repo.has(t)) {
                List<GEN> childGens = new ArrayList<GEN>();
                for (TypeTree<GEN> child : children) {
                    childGens.add(child.data);
                }
                return synth.synthesize(t, childGens);
            }
            return repo.get(t);
        }
    }

    public static <T> TypeTree<T> makeTree(Type type, MakeData<T> make, GeneratorRepository<?> repo) {
        return makeTree(new TypeTree<T>(type, make.make(type)), make, repo);
    }

    private static <T> TypeTree<T> makeTree(TypeTree<T> soFar, MakeData<T> make, GeneratorRepository<?> repo) {
        Type type = soFar.type;
        if (type instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) type).getActualTypeArguments();
            for (Type param : params) {
                TypeTree<T> newNode = new TypeTree<T>(param, make.make(param));
                if (shouldBeSynthesized1(param, repo)) {
                    makeTree(newNode, make, repo);
                }
                soFar.addChild(newNode);
            }
        } else if (Arrays.isArray(type)) {
            Type componentType = GenericTypeReflector.getArrayComponentType(type);
            // TODO: duplication with the previous if block
            TypeTree<T> newNode = new TypeTree<T>(componentType, make.make(componentType));
            if (shouldBeSynthesized1(componentType, repo)) {
                makeTree(newNode, make, repo);
            }
            soFar.addChild(newNode);
        }
        return soFar;
    }

    private static boolean shouldBeSynthesized1(Type type, GeneratorRepository<?> repo) {
        return !repo.has(type) && (Types.hasTypeArguments(type) && type instanceof ParameterizedType)
                || Arrays.isArray(type);
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
