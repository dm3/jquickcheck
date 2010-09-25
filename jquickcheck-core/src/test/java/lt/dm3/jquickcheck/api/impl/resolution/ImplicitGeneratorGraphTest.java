package lt.dm3.jquickcheck.api.impl.resolution;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.impl.resolution.ImplicitGeneratorGraph.Node;
import lt.dm3.jquickcheck.sample.Generator;

import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

@SuppressWarnings("unchecked")
public class ImplicitGeneratorGraphTest {

    @Test
    public void shouldNotFailOnEmptyGraph() {
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        List<Node> satisfied = new ImplicitGeneratorGraph(Collections.<Node> emptyList()).satisfy(repo);

        assertThat(satisfied.isEmpty(), is(true));
    }

    //
    public String b(int x) {
        return String.valueOf(x);
    }

    public int a(String x) {
        return 1;
    }

    @Test
    public void shouldResolveTheDependencies() {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(nodeFrom("b", int.class));
        nodes.add(nodeFrom("a", String.class));
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        given(repo.has(String.class)).willReturn(true);

        List<Node> satisfied = new ImplicitGeneratorGraph(nodes).satisfy(repo);

        // repo returns a generator for a string, the other one depends on it
        assertThat(satisfied.get(0), is(nodes.get(1)));
        assertThat(satisfied.get(1), is(nodes.get(0)));
    }

    @Test
    // same as the previous test, only the order of nodes is backwards to test the satisfied node order.
    public void shouldResolveTheDependenciesInTheCorrectOrder() {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(nodeFrom("a", String.class));
        nodes.add(nodeFrom("b", int.class));
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        given(repo.has(String.class)).willReturn(true);

        List<Node> satisfied = new ImplicitGeneratorGraph(nodes).satisfy(repo);

        assertThat(satisfied.get(0), is(nodes.get(0)));
        assertThat(satisfied.get(1), is(nodes.get(1)));
    }

    //
    public String b(int x, double z) {
        return String.valueOf(x);
    }

    public double a(int x, String y) {
        return 1d;
    }

    public int c(String x) {
        return 1;
    }

    @Test
    public void shouldResolveTheDependenciesWithSeveralIncomingParameters() {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(nodeFrom("b", int.class, double.class));
        nodes.add(nodeFrom("a", int.class, String.class));
        nodes.add(nodeFrom("c", String.class));
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        given(repo.has(String.class)).willReturn(true);

        List<Node> satisfied = new ImplicitGeneratorGraph(nodes).satisfy(repo);

        // repo returns a generator for a string, the others depend on it
        assertThat(satisfied.get(0), is(nodes.get(2)));
        assertThat(satisfied.get(1), is(nodes.get(1)));
        assertThat(satisfied.get(2), is(nodes.get(0)));
    }

    @Test
    public void shouldNotResolveTheDependenciesInCycle() {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(nodeFrom("a", String.class));
        nodes.add(nodeFrom("b", int.class));
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        List<Node> satisfied = new ImplicitGeneratorGraph(nodes).satisfy(repo);

        assertThat(satisfied.isEmpty(), is(true));
    }

    //
    public int makeIntFromListString(List<String> xs) {
        return 1;
    }

    public String makeString() {
        return "oo";
    }

    @Test
    public void shouldResolveSyntheticParameterizedDependencies() {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(nodeFrom("makeString"));
        nodes.add(nodeFrom("makeIntFromListString", List.class));
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);
        given(repo.hasSynthetic(new TypeToken<List<String>>() {}.getType())).willReturn(true);

        List<Node> satisfied = new ImplicitGeneratorGraph(nodes).satisfy(repo);

        assertThat(satisfied.size(), equalTo(2));
        assertThat(satisfied.get(0), is(nodes.get(0)));
        assertThat(satisfied.get(1), is(nodes.get(1)));
    }

    // + makeString
    public int makeIntFromListListString(List<List<String>> xs) {
        return 1;
    }

    @Test
    public void shouldResolveSyntheticParameterizedDependenciesOfTwoLevelsDeep() {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(nodeFrom("makeString"));
        nodes.add(nodeFrom("makeIntFromListListString", List.class));
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        List<Node> satisfied = new ImplicitGeneratorGraph(nodes).satisfy(repo);

        assertThat(satisfied.size(), equalTo(2));
        assertThat(satisfied.get(0), is(nodes.get(0)));
        assertThat(satisfied.get(1), is(nodes.get(1)));
    }

    public int makeIntFromArrayArrayString(String[][] xs) {
        return 1;
    }

    @Test
    public void shouldResolveSyntheticParameterizedDependenciesOfArraysTwoLevelsDeep() {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(nodeFrom("makeString"));
        nodes.add(nodeFrom("makeIntFromArrayArrayString", String[][].class));
        GeneratorRepository<Generator<?>> repo = mock(GeneratorRepository.class);

        List<Node> satisfied = new ImplicitGeneratorGraph(nodes).satisfy(repo);

        assertThat(satisfied.size(), equalTo(2));
        assertThat(satisfied.get(0), is(nodes.get(0)));
        assertThat(satisfied.get(1), is(nodes.get(1)));
    }

    private Node nodeFrom(String methodName, Class<?>... params) {
        try {
            return new Node(this.getClass().getMethod(methodName, params));
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.fail();
        return null;// unreachable
    }
}
