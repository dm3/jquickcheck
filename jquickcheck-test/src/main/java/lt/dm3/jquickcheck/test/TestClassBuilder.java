package lt.dm3.jquickcheck.test;

import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;
import lt.dm3.jquickcheck.sample.SampleProvider;

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
        addClassAnnotation();
    }

    public static <T> TestClassBuilder<T> forJUnit4(String name, Class<? extends T> genClass) {
        return new TestClassBuilder<T>(name, genClass);
    }

    public TestClassBuilder<T> withGenerator(int accessFlag, String fieldClass, String fieldName, String fieldValue) {
        String description = ClassUtils.parameterized(genClass).ofFormatted(fieldClass).build();
        try {
            ClassFile file = clazz.getClassFile();
            ConstPool constPool = file.getConstPool();
            SignatureAttribute sig = new SignatureAttribute(constPool, description);

            clazz.addField(CtField.make(genClass.getName() + " " + fieldName + " = " + fieldValue, clazz));
            FieldInfo field = clazz.getField(fieldName).getFieldInfo();
            field.setAccessFlags(accessFlag);
            field.addAttribute(sig);
        } catch (CannotCompileException e) {
            throw new RuntimeException(String.format("Cannot add field %s of type %s to class %s", fieldName,
                                                     fieldClass, clazz), e);
        } catch (NotFoundException e) {
            throw new RuntimeException(String.format("Cannot add field %s of type %s to class %s", fieldName,
                                                     description, clazz), e);
        }
        return this;
    }

    public TestClassBuilder<T> withProperty(String propertyName, String... parameters) {
        try {
            ClassFile file = clazz.getClassFile();
            ConstPool cPool = file.getConstPool();
            String methodDescriptor = ClassUtils.methodReturning(boolean.class).with(parameters).build();
            MethodInfo method = new MethodInfo(cPool, propertyName, methodDescriptor);
            Bytecode body = new Bytecode(cPool, 0, parameters.length + 1);
            // return true
            body.addIconst(1);
            body.addReturn(CtClass.booleanType);
            method.setCodeAttribute(body.toCodeAttribute());
            method.setAccessFlags(Modifier.PUBLIC);
            AnnotationsAttribute attr = new AnnotationsAttribute(cPool, AnnotationsAttribute.visibleTag);
            Annotation prop = new Annotation(Property.class.getName(), cPool);
            attr.addAnnotation(prop);
            method.addAttribute(attr);
            file.addMethod(method);
        } catch (DuplicateMemberException e) {
            throw new RuntimeException(String.format("Cannot add property %s with parameters of types %s to class %s",
                                                     propertyName,
                                                     Arrays.toString(parameters), clazz), e);
        }
        return this;
    }

    public TestClass build() {
        return new TestClass(clazz);
    }

    private void addClassAnnotation() {
        ClassFile file = clazz.getClassFile();
        ConstPool constPool = file.getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

        Annotation runWith = new Annotation(RunWith.class.getName(), constPool);
        runWith.addMemberValue("value", new ClassMemberValue(QuickCheckRunner.class.getName(), constPool));

        Annotation quickCheck = new Annotation(QuickCheck.class.getName(), constPool);
        quickCheck.addMemberValue("provider", new ClassMemberValue(SampleProvider.class.getName(),
                                                                   constPool));

        attr.addAnnotation(runWith);
        attr.addAnnotation(quickCheck);
        file.addAttribute(attr);
    }
}
