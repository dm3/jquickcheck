package lt.dm3.jquickcheck.api.impl.lookup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.internal.Arrays;
import lt.dm3.jquickcheck.internal.Types;

import com.googlecode.gentyref.GenericTypeReflector;

interface Visitor<T> {
    T visit(Type t, List<TypeTree<T>> children);
}

abstract class ShouldSynthesize {
    boolean should(Type type) {
        return (Types.hasTypeArguments(type) && type instanceof ParameterizedType) || Arrays.isArray(type);
    }
}

class TypeTree<T> {

    private final List<TypeTree<T>> children = new ArrayList<TypeTree<T>>();
    private T contents;
    final Type type;

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
        this.contents = v.visit(type, children);
    }

    T getContents() {
        return contents;
    }

    static <T> TypeTree<T> makeTree(TypeTree<T> node, ShouldSynthesize synthesize) {
        if (node.type instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) node.type).getActualTypeArguments();
            for (Type param : params) {
                TypeTree<T> newNode = new TypeTree<T>(param);
                if (synthesize.should(param)) {
                    makeTree(newNode, synthesize);
                }
                node.addChild(newNode);
            }
        } else if (Arrays.isArray(node.type)) {
            Type componentType = GenericTypeReflector.getArrayComponentType(node.type);
            // TODO: duplication with the previous if block
            TypeTree<T> newNode = new TypeTree<T>(componentType);
            if (synthesize.should(componentType)) {
                makeTree(newNode, synthesize);
            }
            node.addChild(newNode);
        }
        return node;
    }

}
