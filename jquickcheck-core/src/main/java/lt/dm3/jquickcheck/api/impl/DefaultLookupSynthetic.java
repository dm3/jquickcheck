package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.LookupSynthetic;
import lt.dm3.jquickcheck.api.Synthesizer;
import lt.dm3.jquickcheck.internal.Arrays;
import lt.dm3.jquickcheck.internal.Types;

import com.googlecode.gentyref.GenericTypeReflector;

public class DefaultLookupSynthetic<GEN> implements LookupSynthetic<GEN> {

    private final Synthesizer<GEN> synthesizer;
    private final Lookup<Type, GEN> lookupByType;

    public DefaultLookupSynthetic(Synthesizer<GEN> synthesizer, Lookup<Type, GEN> lookupByType) {
        this.synthesizer = synthesizer;
        this.lookupByType = lookupByType;
    }

    private static final class TypeTree<T> {
        private final List<TypeTree<T>> children = new ArrayList<TypeTree<T>>();
        private final Type type;
        private T data;

        TypeTree(Type type) {
            this.type = type;
        }

        void addChild(TypeTree<T> child) {
            this.children.add(child);
        }

        void accept(Visitor<T> v) {
            for (TypeTree<T> child : children) {
                child.accept(v);
            }
            this.data = v.visit(type, children);
        }

    }

    private interface Visitor<T> {
        T visit(Type t, List<TypeTree<T>> children);
    }

    private class CanConstructVisitor implements Visitor<Boolean> {

        @Override
        public Boolean visit(Type t, List<TypeTree<Boolean>> children) {
            if (!lookupByType.has(t)) {
                for (TypeTree<Boolean> child : children) {
                    if (!child.data) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private class CreateGeneratorsVisitor implements Visitor<GEN> {

        @Override
        public GEN visit(Type t, List<TypeTree<GEN>> children) {
            if (!lookupByType.has(t)) {
                List<GEN> childGens = new ArrayList<GEN>();
                for (TypeTree<GEN> child : children) {
                    childGens.add(child.data);
                }
                return synthesizer.synthesize(t, childGens);
            }
            return lookupByType.get(t);
        }
    }

    private <T> TypeTree<T> makeTree(TypeTree<T> node) {
        if (node.type instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) node.type).getActualTypeArguments();
            for (Type param : params) {
                TypeTree<T> newNode = new TypeTree<T>(param);
                if (shouldBeSynthesized(param)) {
                    makeTree(newNode);
                }
                node.addChild(newNode);
            }
        } else if (Arrays.isArray(node.type)) {
            Type componentType = GenericTypeReflector.getArrayComponentType(node.type);
            // TODO: duplication with the previous if block
            TypeTree<T> newNode = new TypeTree<T>(componentType);
            if (shouldBeSynthesized(componentType)) {
                makeTree(newNode);
            }
            node.addChild(newNode);
        }
        return node;
    }

    private boolean shouldBeSynthesized(Type type) {
        return !lookupByType.has(type) && (Types.hasTypeArguments(type) && type instanceof ParameterizedType)
                || Arrays.isArray(type);
    }

    @Override
    public boolean hasSynthetic(Type t) {
        TypeTree<Boolean> tree = makeTree(new TypeTree<Boolean>(t));
        tree.accept(new CanConstructVisitor());
        return tree.data;
    }

    @Override
    public GEN getSynthetic(Type t) {
        TypeTree<GEN> tree = makeTree(new TypeTree<GEN>(t));
        tree.accept(new CreateGeneratorsVisitor());
        // TODO: throw exception if null?
        return tree.data;
    }

}
