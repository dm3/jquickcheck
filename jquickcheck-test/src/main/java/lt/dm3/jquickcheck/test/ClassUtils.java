package lt.dm3.jquickcheck.test;

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
            return of(clazz.getName());
        }

        ClassNameBuilder of(String className) {
            return new ClassNameBuilder(this.name.substring(0, this.name.length() - 1) + "<"
                    + Descriptor.of(className) + ">;");
        }

        String build() {
            return name;
        }
    }

    public static String newInstance(Class<?> clazz) {
        return "new " + clazz.getName() + "()";
    }

    public static ClassNameBuilder classNameOf(Class<?> clazz) {
        return new ClassNameBuilder(Descriptor.of(clazz.getName()));
    }

}
