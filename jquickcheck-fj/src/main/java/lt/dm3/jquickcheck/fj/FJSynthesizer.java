package lt.dm3.jquickcheck.fj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import lt.dm3.jquickcheck.api.impl.DefaultSynthesizer;
import fj.test.Arbitrary;

public class FJSynthesizer extends DefaultSynthesizer<Arbitrary<?>> {

    private static final Iterable<Synthesized<Arbitrary<?>>> SYNTHESIZEDS;

    static {
        // ----------- java util
        List<Synthesized<Arbitrary<?>>> synths = new ArrayList<Synthesized<Arbitrary<?>>>();
        synths.add(new AbstractSynthesized<Arbitrary<?>>(ArrayList.class) {
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbArrayList(components.get(0));
            }
        });
        // hash
        synths.add(new AbstractSynthesized<Arbitrary<?>>(HashMap.class) {
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbHashMap(components.get(0), components.get(1));
            }
        });
        synths.add(new AbstractSynthesized<Arbitrary<?>>(HashSet.class) {
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbHashSet(components.get(0));
            }
        });
        // tree
        synths.add(new AbstractSynthesized<Arbitrary<?>>(TreeMap.class) {
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbTreeMap(components.get(0), components.get(1));
            }
        });
        synths.add(new AbstractSynthesized<Arbitrary<?>>(TreeSet.class) {
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbTreeSet(components.get(0));
            }
        });
        // linked
        synths.add(new AbstractSynthesized<Arbitrary<?>>(LinkedList.class) {
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbLinkedList(components.get(0));
            }
        });
        synths.add(new AbstractSynthesized<Arbitrary<?>>(LinkedHashMap.class) {
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbLinkedHashMap(components.get(0), components.get(1));
            }
        });
        synths.add(new AbstractSynthesized<Arbitrary<?>>(LinkedHashSet.class) {
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbLinkedHashSet(components.get(0));
            }
        });
        // enum
        synths.add(new AbstractSynthesized<Arbitrary<?>>(EnumMap.class) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbEnumMap((Arbitrary) components.get(0), components.get(1));
            }
        });
        synths.add(new AbstractSynthesized<Arbitrary<?>>(EnumSet.class) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbEnumSet((Arbitrary) components.get(0));
            }
        });
        synths.add(new AbstractSynthesized<Arbitrary<?>>(EnumSet.class) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public Arbitrary<?> synthesize(List<Arbitrary<?>> components) {
                return Arbitrary.arbEnumSet((Arbitrary) components.get(0));
            }
        });
        SYNTHESIZEDS = Collections.unmodifiableList(synths);
    }

    public FJSynthesizer() {
        super(SYNTHESIZEDS);
    }

}
