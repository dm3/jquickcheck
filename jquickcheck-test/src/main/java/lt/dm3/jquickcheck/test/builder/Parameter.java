package lt.dm3.jquickcheck.test.builder;

import java.lang.reflect.Type;

public class Parameter {

    private final String description;

    Parameter(String description) {
        this.description = description;
    }

    public static Parameter describedBy(String description) {
        return new Parameter(description);
    }

    public static Parameter of(Type type) {
        return describedBy(ClassUtils.describe(type));
    }

    public static String toDescription(Iterable<Parameter> params) {
        StringBuilder result = new StringBuilder("(");
        for (Parameter param : params) {
            result.append(param);
        }
        return result.append(")").toString();
    }

    @Override
    public String toString() {
        return description;
    }

}
