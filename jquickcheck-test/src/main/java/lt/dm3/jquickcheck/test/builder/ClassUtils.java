package lt.dm3.jquickcheck.test.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javassist.bytecode.Descriptor;

public abstract class ClassUtils {

    private ClassUtils() {
        // static utils
    }

    /**
     * For now only supports two level deep generic class names.
     */
    static final class ClassNameBuilder {

        private final String name;

        ClassNameBuilder(String className) {
            this.name = className;
        }

        ClassNameBuilder of(Class<?> clazz) {
            return new ClassNameBuilder(prune(this.name) + "<" + Descriptor.of(clazz.getName()) + ">;");
        }

        ClassNameBuilder ofFormatted(String className) {
            return new ClassNameBuilder(prune(this.name) + "<" + className + ">;");
        }

        private String prune(String toPrune) {
            return toPrune.substring(0, toPrune.length() - 1);
        }

        String build() {
            return name;
        }

    }

    public static String newInstance(Class<?> clazz) {
        return "new " + clazz.getName() + "();";
    }

    public static ClassNameBuilder parameterized(Class<?> clazz) {
        return new ClassNameBuilder(Descriptor.of(clazz.getName()));
    }

    public static String describe(Type t) {
        if (t instanceof Class<?>) {
            return Descriptor.of(((Class) t).getName());
        } else if (t instanceof ParameterizedType) {
            ParameterizedType pType = ((ParameterizedType) t);
            Type[] args = pType.getActualTypeArguments();
            StringBuilder formatted = new StringBuilder();
            for (Type type : args) {
                formatted.append(describe(type));
            }
            return parameterized((Class) pType.getRawType()).ofFormatted(formatted.toString()).build();
        }
        return "";
    }
}
