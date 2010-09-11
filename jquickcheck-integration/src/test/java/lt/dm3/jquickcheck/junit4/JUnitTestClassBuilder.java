package lt.dm3.jquickcheck.junit4;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import lt.dm3.jquickcheck.test.builder.AbstractTestClassBuilder;

import org.junit.runner.RunWith;

public abstract class JUnitTestClassBuilder<T> extends AbstractTestClassBuilder<T> {
    protected JUnitTestClassBuilder(String name, Class<? super T> genClass) {
        super(name, genClass);
    }

    @Override
    protected void addClassLevelAnnotation(AnnotationsAttribute attribute, ConstPool constPool) {
        Annotation runWith = new Annotation(RunWith.class.getName(), constPool);
        runWith.addMemberValue("value", new ClassMemberValue(QuickCheckRunner.class.getName(), constPool));
        attribute.addAnnotation(runWith);
    }

}
