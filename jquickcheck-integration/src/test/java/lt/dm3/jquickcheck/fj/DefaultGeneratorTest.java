package lt.dm3.jquickcheck.fj;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import lt.dm3.jquickcheck.api.impl.TypeResolverRegistry;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;
import lt.dm3.jquickcheck.test.AbstractDefaultGeneratorTest;
import lt.dm3.jquickcheck.test.AbstractTestClassBuilder;
import lt.dm3.jquickcheck.test.GeneratorInfo;
import lt.dm3.jquickcheck.test.TestClassBuilderFactory;

import org.junit.runner.RunWith;

import fj.test.Arbitrary;
import fj.test.Gen;

@SuppressWarnings("rawtypes")
public class DefaultGeneratorTest extends AbstractDefaultGeneratorTest<Gen> {

    @Override
    protected Iterable<GeneratorInfo> defaultGenerators() {
        List<GeneratorInfo> result = new ArrayList<GeneratorInfo>();
        Field[] arbFields = Arbitrary.class.getFields();
        for (Field f : arbFields) {
            if (f.getName().startsWith("arb")) {
                result.add(arbitraryFrom(f));
            }
        }
        return result;
    }

    private static GeneratorInfo arbitraryFrom(Field f) {
        Type t = TypeResolverRegistry.resolveFrom(f);
        return new GeneratorInfo("fj/test/Arbitrary." + f.getName() + ";", t);
    }

    private static class JUnitTestClassBuilder extends AbstractTestClassBuilder<Gen> {
        protected JUnitTestClassBuilder(String name, Class<? super Gen> genClass) {
            super(name, genClass);
        }

        @Override
        protected Class<?> getQuickCheckProviderClass() {
            return FJ.class;
        }

        @Override
        protected void addClassLevelAnnotation(AnnotationsAttribute attribute, ConstPool constPool) {
            Annotation runWith = new Annotation(RunWith.class.getName(), constPool);
            runWith.addMemberValue("value", new ClassMemberValue(QuickCheckRunner.class.getName(), constPool));
            attribute.addAnnotation(runWith);
        }

    }

    @Override
    protected TestClassBuilderFactory<Gen> defaultClassBuilderFactory() {
        return new TestClassBuilderFactory<Gen>() {
            @Override
            public AbstractTestClassBuilder<Gen> createBuilder(String className, Class<? super Gen> generatorClass) {
                return new JUnitTestClassBuilder(className, generatorClass);
            }
        };
    }

    @Override
    protected Class<Gen> generatorClass() {
        return Gen.class;
    }
}
