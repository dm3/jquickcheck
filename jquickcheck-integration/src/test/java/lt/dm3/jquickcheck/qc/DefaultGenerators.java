package lt.dm3.jquickcheck.qc;

import java.util.Arrays;
import java.util.Iterator;

import lt.dm3.jquickcheck.test.builder.GeneratorInfo;
import net.java.quickcheck.generator.PrimitiveGenerators;

public class DefaultGenerators implements Iterable<GeneratorInfo> {

    @Override
    public Iterator<GeneratorInfo> iterator() {
        return Arrays.asList(new GeneratorInfo(PrimitiveGenerators.class.getName() + ".integers();", Integer.class))
                .iterator();
    }

}
