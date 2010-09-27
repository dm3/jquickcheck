package lt.dm3.jquickcheck.api.impl.lookup;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.LookupSynthetic;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.Synthesizer;

public class DefaultLookupSynthetic<GEN> extends DefaultContainsSynthetic implements LookupSynthetic<GEN> {

    private final Synthesizer<GEN> synthesizer;
    private final Lookup<Type, GEN> lookupByType;

    public DefaultLookupSynthetic(Synthesizer<GEN> synthesizer, Lookup<Type, GEN> lookupByType) {
        super(lookupByType);
        this.synthesizer = synthesizer;
        this.lookupByType = lookupByType;
    }

    private class CreateGeneratorsVisitor implements Visitor<GEN> {

        @Override
        public GEN visit(Type t, List<TypeTree<GEN>> children) {
            if (!lookupByType.has(t)) {
                List<GEN> childGens = new ArrayList<GEN>();
                for (TypeTree<GEN> child : children) {
                    childGens.add(child.getContents());
                }
                return synthesizer.synthesize(t, childGens);
            }
            return lookupByType.get(t);
        }
    }

    @Override
    public GEN getSynthetic(Type t) {
        TypeTree<GEN> tree = TypeTree.makeTree(new TypeTree<GEN>(t), new ShouldSynthesize() {
            @Override
            public boolean should(Type type) {
                return !lookupByType.has(type) && super.should(type);
            }
        });
        tree.accept(new CreateGeneratorsVisitor());
        if (tree.getContents() == null) {
            throw new QuickCheckException("Could not synthesize a generator of type: " + t);
        }
        return tree.getContents();
    }

}
