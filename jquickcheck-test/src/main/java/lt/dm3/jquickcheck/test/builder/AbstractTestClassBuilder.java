package lt.dm3.jquickcheck.test.builder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.impl.DefaultInvocationSettings;

/**
 * A builder which builds test classes containing generators. Built test classes should later be used in tests for
 * different test provider/quickcheck backend combinations. The main idea is to supply the builder with the name of a
 * class, the test engine and the set of generators to be placed inside of the test.
 * 
 * @author dm3
 * 
 * @param <T>
 */
public abstract class AbstractTestClassBuilder<T> {

    private final CtClass clazz;
    private final Class<? super T> genClass;
    private boolean useDefaults = DefaultInvocationSettings.DEFAULT_USE_DEFAULTS;

    protected AbstractTestClassBuilder(String name, Class<? super T> genClass) {
        ClassPool cPool = ClassPool.getDefault();
        this.clazz = cPool.makeClass(name);
        this.genClass = genClass;
    }

    public AbstractTestClassBuilder<T> withGenerator(int accessFlag, String fieldClass, String fieldName,
                                                     String fieldValue) {
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

    public PropertyBuilder<T> withProperty(String propertyName) {
        return new PropertyBuilder<T>(this, propertyName);
    }

    public PropertyBuilder<T> withRandomProperty() {
        return new PropertyBuilder<T>(this, RandomUtils.randomJavaIdentifier());
    }

    public AbstractTestClassBuilder<T> useDefaults() {
        this.useDefaults = true;
        return this;
    }

    public GeneratedTest build() {
        addClassAnnotation();
        return new GeneratedTest(clazz);
    }

    private void addClassAnnotation() {
        ClassFile file = clazz.getClassFile();
        ConstPool constPool = file.getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        addClassLevelAnnotation(attr, constPool);

        Annotation quickCheck = new Annotation(QuickCheck.class.getName(), constPool);
        quickCheck.addMemberValue("provider", new ClassMemberValue(getQuickCheckProviderClass().getName(),
                                                                   constPool));
        quickCheck.addMemberValue("useDefaults", new BooleanMemberValue(useDefaults, constPool));

        attr.addAnnotation(quickCheck);
        file.addAttribute(attr);
    }

    // default scope
    CtClass getCtClass() {
        return clazz;
    }

    protected abstract Class<?> getQuickCheckProviderClass();

    protected abstract void addClassLevelAnnotation(AnnotationsAttribute attribute, ConstPool constPool);

}
