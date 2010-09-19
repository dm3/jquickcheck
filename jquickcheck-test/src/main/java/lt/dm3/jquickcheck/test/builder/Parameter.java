package lt.dm3.jquickcheck.test.builder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

public class Parameter {

    /**
     * not an Annotation as we already hava java.lang.*Annotation and javassist.*Annotation.
     */
    public class Ann {

        private final Class<? extends java.lang.annotation.Annotation> clazz;
        // Cannot put MemberValue here as a second parameter as MemberValue requires a ClassPool which doesn't exist at
        // a time when Parameter objects are created.
        private final Map<String, String> stringMembers = new HashMap<String, String>();

        private Ann(Class<? extends java.lang.annotation.Annotation> annotation) {
            this.clazz = annotation;
        }

        public Ann with(String param, String value) {
            stringMembers.put(param, value);
            return this;
        }

        public Parameter end() {
            return Parameter.this;
        }

        Class<? extends java.lang.annotation.Annotation> getClazz() {
            return clazz;
        }

    }

    private final String description;
    // It's too tiresome to have both javassists' Annotation and the java.langs' one in
    // so the upper bound of Class' type parameter had to go.
    private final List<Ann> annotations = new ArrayList<Ann>();

    Parameter(String description) {
        this.description = description;
    }

    public static Parameter describedBy(String description) {
        return new Parameter(description);
    }

    public static Parameter of(Type type) {
        return describedBy(ClassUtils.describe(type));
    }

    public Ann annotatedBy(Class<? extends java.lang.annotation.Annotation> annotation) {
        Ann ann = new Ann(annotation);
        this.annotations.add(ann);
        return ann;
    }

    /**
     * Has a nasty side-effect of appending the {@link ParameterAnnotationsAttribute} to the received propertyBuilder. <br />
     * Returns a description of a property built by an enclosing (argument) property builder with parameters: <br />
     * (Ljava/lang/Integer;Ljava/lang/String)Z
     * 
     * @param params
     * @param propertyBuilder
     * @return a java class file compatible description of the method parameters and the return type
     */
    static String toDescription(List<Parameter> params, PropertyBuilder<?> propertyBuilder) {
        ConstPool pool = propertyBuilder.getCtClass().getClassFile2().getConstPool();
        ParameterAnnotationsAttribute paramAnn = new ParameterAnnotationsAttribute(pool,
                ParameterAnnotationsAttribute.visibleTag);
        paramAnn.setAnnotations(collectAnnotations(params, pool));

        StringBuilder result = new StringBuilder("(");
        for (Parameter param : params) {
            result.append(param);
        }
        propertyBuilder.addAttribute(paramAnn);
        return result.append(")").append(ClassUtils.describe(propertyBuilder.getReturnedValue())).toString();
    }

    private static Annotation[][] collectAnnotations(List<Parameter> params, ConstPool pool) {
        Annotation[][] anns = new Annotation[params.size()][];
        int currParam = 0;
        for (Parameter param : params) {
            Annotation[] paramAnns = new Annotation[param.annotations.size()];
            int currAnn = 0;
            for (Ann annotation : param.annotations) {
                Annotation ann = new Annotation(annotation.getClazz().getName(), pool);
                for (Map.Entry<String, String> member : annotation.stringMembers.entrySet()) {
                    ann.addMemberValue(member.getKey(), new StringMemberValue(member.getValue(), pool));
                }
                paramAnns[currAnn++] = ann;
            }
            anns[currParam++] = paramAnns;
        }
        return anns;
    }

    @Override
    public String toString() {
        return description;
    }

}
