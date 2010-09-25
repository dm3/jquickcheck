package lt.dm3.jquickcheck.api.impl.resolution;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.internal.Types;

import com.googlecode.gentyref.GenericTypeReflector;

class ImplicitGeneratorGraph {

    static final class Node {
        private final Type produced;
        private final Method method;

        private boolean isDefault;

        public Node(Method method) {
            this.produced = method.getGenericReturnType();
            this.method = method;
        }

        public boolean dependsOn(Node other) {
            for (Type t : method.getGenericParameterTypes()) {
                if (other.produced.equals(t)) {
                    return true;
                }
            }
            return false;
        }

        public boolean satisfiedBy(GeneratorRepository<?> repo, Iterable<Node> satisfied) {
            for (Type t : method.getGenericParameterTypes()) {
                boolean found = false;
                if (repo.has(t)) {
                    found = true;
                } else if (repo.hasDefault(t)) {
                    isDefault = true;
                    found = true;
                } else if (repo.hasSyntheticForClass(GenericTypeReflector.erase(t))) {
                    if (Types.hasTypeArguments(t)) {
                        for (Type arg : ((ParameterizedType) t).getActualTypeArguments()) {
                            if (!repo.has(arg)) {
                                return false;
                            }
                        }
                    }
                    // TODO: synthetics are currently treated as normals
                    found = true;
                } else if (satisfiedByOtherNodes(t, satisfied)) {
                    found = true;
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        }

        private boolean satisfiedByOtherNodes(Type t, Iterable<Node> satisfied) {
            for (Node n : satisfied) {
                if (n.produced.equals(t)) {
                    if (n.isDefault) {
                        isDefault = true;
                    }
                    return true;
                }
            }
            return false;
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
    private final List<Node>[] edges;

    /**
     * @param size
     *            number of nodes
     */
    @SuppressWarnings("unchecked")
    public ImplicitGeneratorGraph(List<Node> allNodes) {
        int size = allNodes.size();
        nodes = new Node[size];
        edges = new List[size];
        for (int i = 0; i < size; i++) {
            Node n = allNodes.get(i);
            nodes[i] = n;
            edges[i] = new LinkedList<Node>();
            for (int j = 0; j < i; j++) {
                Node other = nodes[j];
                if (n.dependsOn(other)) {
                    edges[j].add(n);
                } else if (other.dependsOn(n)) {
                    edges[i].add(other);
                }
            }
        }
    }

    /**
     * Not-so-efficient algorithm: fold over the nodes with a set of possibly satisfied nodes (initially empty, mutable)
     * and recurse on each satisfied node while adding the node to the set of satisfied nodes.
     * 
     * @param repo
     * @return
     */
    public List<Node> satisfy(GeneratorRepository<?> repo) {
        List<Node> satisfied = new LinkedList<Node>();
        satisfy(this, satisfied, repo);
        return satisfied;
    }

    private static <GEN> void satisfy(ImplicitGeneratorGraph graph, List<Node> satisfied, GeneratorRepository<GEN> repo) {
        for (int i = 0; i < graph.nodes.length; i++) {
            Node n = graph.nodes[i];
            if (!satisfied.contains(n) && n.satisfiedBy(repo, satisfied)) {
                satisfied.add(n);
                satisfy(graph, satisfied, repo);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Nodes: " + Arrays.toString(nodes) + "\n");
        for (int i = 0; i < nodes.length; i++) {
            result.append(nodes[i] + ": \n");
            for (Node edge : edges[i]) {
                result.append("\t->" + edge);
            }
            result.append("\n");
        }
        return result.toString();
    }
}
