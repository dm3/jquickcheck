package lt.dm3.jquickcheck.test;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;

import org.junit.runner.RunWith;

public class TestClassBuilder {

    public void build(String name) {
        ClassPool cPool = ClassPool.getDefault();
        CtClass clazz = cPool.makeClass(name);
        ClassFile file = clazz.getClassFile();
        ConstPool constPool = file.getConstPool();

        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation runWith = new Annotation(RunWith.class.getName(), constPool);
        runWith.addMemberValue("value", new ClassMemberValue(QuickCheckRunner.class.getName(), constPool));
        attr.setAnnotation(runWith);
        file.addAttribute(attr);

        try {
            clazz.toClass();
        } catch (CannotCompileException e) {
            throw new RuntimeException("Could not create class " + name, e);
        }
    }

    public static TestClassBuilder forJUnit4() {
        return new TestClassBuilder();
    }
}
