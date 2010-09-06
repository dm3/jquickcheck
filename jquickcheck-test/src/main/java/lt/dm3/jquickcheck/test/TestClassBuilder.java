package lt.dm3.jquickcheck.test;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;

import org.junit.runner.RunWith;

/**
 * A builder which builds test classes containing generators. Built test classes should later be used in tests for
 * different test provider/quickcheck backend combinations. The main idea is to supply the builder with the name of a
 * class, the test engine and the set of generators to be placed inside of the test.
 * 
 * @author dm3
 * 
 * @param <T>
 */
public class TestClassBuilder<T> {

    private final CtClass clazz;
    private final Class<? extends T> genClass;

    public TestClassBuilder(String name, Class<? extends T> genClass) {
        ClassPool cPool = ClassPool.getDefault();
        this.clazz = cPool.makeClass(name);
        this.genClass = genClass;
    }

    public static <T> TestClassBuilder<T> forJUnit4(String name, Class<? extends T> genClass) {
        return new TestClassBuilder<T>(name, genClass);
    }

    public TestClassBuilder<T> withGenerator(String fieldClass, String fieldName, String fieldValue, int accessFlag) {
        try {
            ClassFile file = clazz.getClassFile();
            ConstPool constPool = file.getConstPool();
            String genClassDescriptor = Descriptor.of(genClass.getName());
            FieldInfo field = new FieldInfo(constPool, fieldName, genClassDescriptor);
            String description = ClassUtils.classNameOf(genClass).of(fieldClass).build();
            SignatureAttribute sig = new SignatureAttribute(constPool, description);
            field.setAccessFlags(accessFlag);
            field.addAttribute(sig);
            file.addField(field);
            CtField newField = clazz.getField(fieldName);
            // hack - need to remove field in order to assign it back with a value
            clazz.removeField(newField);
            clazz.addField(newField, fieldValue);
        } catch (CannotCompileException e) {
            throw new RuntimeException(String.format("Cannot add field %s of type %s to class %s", fieldName,
                                                     fieldClass, clazz), e);
        } catch (NotFoundException e) {
            throw new RuntimeException(String.format("Cannot add field %s of type %s to class %s", fieldName,
                                                     fieldClass, clazz), e);
        }
        return this;
    }

    public void build() {
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
            throw new RuntimeException("Could not create class " + clazz, e);
        }
    }
}
