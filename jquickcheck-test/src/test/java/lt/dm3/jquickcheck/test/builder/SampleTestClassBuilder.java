package lt.dm3.jquickcheck.test.builder;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import lt.dm3.jquickcheck.sample.SampleProvider;
import lt.dm3.jquickcheck.sample.SampleRunner;
import lt.dm3.jquickcheck.test.builder.AbstractTestClassBuilder;

import org.junit.runner.RunWith;

public class SampleTestClassBuilder<T> extends AbstractTestClassBuilder<T> {

    protected SampleTestClassBuilder(String name, Class<? super T> genClass) {
        super(name, genClass);
    }

    public static <T> AbstractTestClassBuilder<T> forSample(String name, Class<? super T> genClass) {
        return new SampleTestClassBuilder<T>(name, genClass);
    }

    @Override
    protected void addClassLevelAnnotation(AnnotationsAttribute attribute, ConstPool constPool) {
        Annotation runWith = new Annotation(RunWith.class.getName(), constPool);
        runWith.addMemberValue("value", new ClassMemberValue(SampleRunner.class.getName(), constPool));
        attribute.addAnnotation(runWith);
    }

    @Override
    protected Class<?> getQuickCheckProviderClass() {
        return SampleProvider.class;
    }

}
