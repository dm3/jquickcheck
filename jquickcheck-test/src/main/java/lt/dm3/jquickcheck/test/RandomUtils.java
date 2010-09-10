package lt.dm3.jquickcheck.test;

import java.util.UUID;

abstract class RandomUtils {

    private RandomUtils() {
        // static utils
    }

    public static String randomJavaIdentifier() {
        return "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
