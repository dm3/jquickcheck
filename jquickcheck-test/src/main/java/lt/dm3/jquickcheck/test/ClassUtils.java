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
            return new ClassNameBuilder(prune(this.name) + "<" + Descriptor.of(clazz.getName()) + ">;");
        }

        ClassNameBuilder ofFormatted(String className) {
            return new ClassNameBuilder(prune(this.name) + "<" + className // (isGeneric(className) ? prune(className) :
                                                                           // className)
                    + ">;");
        }

        private boolean isGeneric(String className) {
            return className.endsWith(">;");
        }

        private String prune(String toPrune) {
            return toPrune.substring(0, toPrune.length() - 1);
        }

        String build() {
            return name;
        }

    }

    static final class MethodBuilder {

        private final String returns;
        private final String[] params;

        MethodBuilder(String returns, String... params) {
            this.returns = returns;
            this.params = params;
        }

        MethodBuilder with(String[] parameters) {
            String[] result = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                result[i] = Descriptor.of(parameters[i]);
            }
            return new MethodBuilder(returns, result);
        }

        String build() {
            StringBuilder result = new StringBuilder("(");
            for (String param : params) {
                result.append(param);
            }
            return result.append(")").append(returns).toString();
        }

    }

    public static String newInstance(Class<?> clazz) {
        return "new " + clazz.getName() + "()";
    }

    public static ClassNameBuilder parameterized(Class<?> clazz) {
        return new ClassNameBuilder(Descriptor.of(clazz.getName()));
    }

    public static MethodBuilder methodReturning(Class<?> clazz) {
        return new MethodBuilder(Descriptor.of(clazz.getName()));
    }

}
