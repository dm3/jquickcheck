package lt.dm3.jquickcheck.test.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.CtClass;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import lt.dm3.jquickcheck.Property;

public class PropertyBuilder<T> {

    private final String name;
    private final AbstractTestClassBuilder<T> parentBuilder;
    private final CtClass owner;
    private boolean returns = true;
    private final List<Parameter> params = new ArrayList<Parameter>();

    PropertyBuilder(AbstractTestClassBuilder<T> parentBuilder, String name) {
        this.parentBuilder = parentBuilder;
        this.owner = parentBuilder.getCtClass();
        this.name = name;
    }

    public PropertyBuilder<T> returning(boolean value) {
        this.returns = value;
        return this;
    }

    public PropertyBuilder<T> with(Parameter[] param) {
        this.params.addAll(Arrays.asList(param));
        return this;
    }

    public PropertyBuilder<T> with(Parameter param) {
        this.params.add(param);
        return this;
    }

    public AbstractTestClassBuilder<T> and() {
        try {
            ClassFile ownerFile = owner.getClassFile();
            ConstPool ownerPool = ownerFile.getConstPool();
            String methodDescriptor = Parameter.toDescription(params) + ClassUtils.describe(boolean.class);
            MethodInfo method = new MethodInfo(ownerPool, name, methodDescriptor);
            Bytecode body = new Bytecode(ownerPool, 0, params.size() + 1);
            body.addIconst(returns ? 1 : 0); // 1 - true, 0 - false
            body.addReturn(CtClass.booleanType);
            method.setCodeAttribute(body.toCodeAttribute());
            method.setAccessFlags(Modifier.PUBLIC);
            AnnotationsAttribute attr = new AnnotationsAttribute(ownerPool, AnnotationsAttribute.visibleTag);
            Annotation prop = new Annotation(Property.class.getName(), ownerPool);
            attr.addAnnotation(prop);
            method.addAttribute(attr);
            ownerFile.addMethod(method);
        } catch (DuplicateMemberException e) {
            throw new RuntimeException(String.format("Cannot add property %s with parameters of types %s to class %s",
                                                     name,
                                                     params, owner), e);
        }
        return parentBuilder;
    }

}
