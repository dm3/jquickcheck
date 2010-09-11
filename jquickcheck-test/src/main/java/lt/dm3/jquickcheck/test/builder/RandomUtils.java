package lt.dm3.jquickcheck.test.builder;

import java.util.UUID;

public abstract class RandomUtils {

    private RandomUtils() {
        // static utils
    }

    public static String randomJavaIdentifier() {
        return "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
