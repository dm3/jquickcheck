package lt.dm3.jquickcheck.api.impl.lookup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import lt.dm3.jquickcheck.api.ContainsSynthetic;
import lt.dm3.jquickcheck.api.LookupContains;
import lt.dm3.jquickcheck.internal.Arrays;

public class DefaultContainsSynthetic implements ContainsSynthetic {

    private class CanConstructVisitor implements Visitor<Boolean> {

        @Override
        public Boolean visit(Type t, List<TypeTree<Boolean>> children) {
            if (children.isEmpty()) {
                // leaf node
                return containsByType.has(t);
            }
            if (!containsByType.has(t)) {
                for (TypeTree<Boolean> child : children) {
                    if (!child.getContents()) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private final LookupContains<Type> containsByType;

    public DefaultContainsSynthetic(LookupContains<Type> lookupByType) {
        this.containsByType = lookupByType;
    }

    @Override
    public boolean hasSynthetic(Type t) {
        if (!validate(t)) {
            return false;
        }

        TypeTree<Boolean> tree = TypeTree.makeTree(new TypeTree<Boolean>(t), new ShouldSynthesize() {
            @Override
            public boolean should(Type type) {
                return !containsByType.has(type) && super.should(type);
            }
        });
        tree.accept(new CanConstructVisitor());
        return tree.getContents();
    }

    private boolean validate(Type t) {
        return (t instanceof ParameterizedType) || Arrays.isArray(t);
    }

}
