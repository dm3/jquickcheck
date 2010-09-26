package lt.dm3.jquickcheck.api.impl.resolution;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lt.dm3.jquickcheck.api.RepositoryContains;
import lt.dm3.jquickcheck.api.impl.lookup.DefaultContainsSynthetic;
import lt.dm3.jquickcheck.internal.Primitives;

class ImplicitGeneratorGraph {
    private static class RepositoryView<GEN> implements RepositoryContains {
        private final List<Node> satisfied;
        private final RepositoryContains underlying;

        public RepositoryView(List<Node> satisfied, RepositoryContains repo) {
            this.satisfied = satisfied;
            this.underlying = repo;
        }

        public boolean has(Type t) {
            return underlying.has(t) || satisfiedByOtherNodes(t, satisfied);
        }

        public boolean has(String name) {
            return underlying.has(name);
        }

        public boolean hasDefault(Type t) {
            return underlying.hasDefault(t) || satisfiedByOtherDefaultNodes(t, satisfied);
        }

        @Override
        public boolean hasSynthetic(Type t) {
            return new DefaultContainsSynthetic(this).hasSynthetic(t);
        }

        private boolean satisfiedByOtherNodes(Type t, Iterable<Node> satisfied) {
            for (Node n : satisfied) {
                if (Primitives.equalIgnoreWrapping(n.produced, t) && !n.isDefault) {
                    return true;
                }
            }
            return false;
        }

        private boolean satisfiedByOtherDefaultNodes(Type t, Iterable<Node> satisfied) {
            for (Node n : satisfied) {
                if (Primitives.equalIgnoreWrapping(n.produced, t) && n.isDefault) {
                    return true;
                }
            }
            return false;
        }

    }

    static final class Node {
        private final Type produced;
        private final Method method;

        private boolean isDefault;

        public Node(Method method) {
            this.produced = method.getGenericReturnType();
            this.method = method;
        }

        public boolean satisfiedBy(RepositoryContains repo, Iterable<Node> satisfied) {
            for (Type t : method.getGenericParameterTypes()) {
                boolean found = false;
                if (repo.has(t)) {
                    found = true;
                } else if (repo.hasDefault(t)) {
                    isDefault = true;
                    found = true;
                } else if (repo.hasSynthetic(t)) {
                    // TODO: synthetics are currently treated as normals
                    found = true;
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        }

        Type getType() {
            return produced;
        }

        boolean isDefault() {
            return isDefault;
        }

        String getName() {
            return method.getName();
        }

        Method getMethod() {
            return method;
        }

        @Override
        public String toString() {
            return "( " + method + " produces " + produced + ", needs: "
                    + Arrays.toString(method.getGenericParameterTypes()) + " )";
        }

    }

    private final Node[] nodes;

    /**
     * @param allNodes
     *            nodes
     */
    public ImplicitGeneratorGraph(List<Node> allNodes) {
        int size = allNodes.size();
        nodes = new Node[size];
        for (int i = 0; i < size; i++) {
            Node n = allNodes.get(i);
            nodes[i] = n;
        }
    }

    /**
     * Not-so-efficient algorithm: fold over the nodes with a set of possibly satisfied nodes (initially empty, mutable)
     * and recurse on each satisfied node while adding the node to the set of satisfied nodes.
     * 
     * @param repo
     * @return
     */
    public <T> List<Node> satisfy(RepositoryContains repo) {
        List<Node> satisfied = new LinkedList<Node>();
        satisfy(this, satisfied, new RepositoryView<T>(satisfied, repo));
        return satisfied;
    }

    private static <GEN> void satisfy(ImplicitGeneratorGraph graph, List<Node> satisfied, RepositoryContains repo) {
        for (int i = 0; i < graph.nodes.length; i++) {
            Node n = graph.nodes[i];
            if (!satisfied.contains(n) && n.satisfiedBy(repo, satisfied)) {
                satisfied.add(n);
                satisfy(graph, satisfied, repo);
            }
        }
    }

}
