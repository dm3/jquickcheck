package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javassist.Modifier;
import lt.dm3.jquickcheck.api.impl.TypeResolverRegistry;
import lt.dm3.jquickcheck.test.builder.GeneratorInfo;
import fj.test.Arbitrary;

/**
 * Currently only generates generators specified in the static fields of {@link Arbitrary}. <br />
 * TODO: also generate generators by using static arb* methods.
 * 
 * @author dm3
 * 
 */
public class DefaultGenerators implements Iterable<GeneratorInfo> {

    @Override
    public Iterator<GeneratorInfo> iterator() {
        List<GeneratorInfo> result = new ArrayList<GeneratorInfo>();
        Field[] arbFields = Arbitrary.class.getFields();
        for (Field f : arbFields) {
            if (f.getName().startsWith("arb") && Modifier.isStatic(f.getModifiers())) {
                result.add(arbitraryFrom(f));
            }
        }
        return result.iterator();
    }

    private static GeneratorInfo arbitraryFrom(Field f) {
        Type t = TypeResolverRegistry.resolveFrom(f);
        return new GeneratorInfo(Arbitrary.class.getName() + "." + f.getName() + ";", t);
    }

}
