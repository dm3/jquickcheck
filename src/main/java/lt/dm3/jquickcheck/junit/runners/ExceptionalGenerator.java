package lt.dm3.jquickcheck.junit.runners;

import java.util.Random;

public class ExceptionalGenerator implements Generator<Object> {

    @Override
    public Object generate(Random r, int size) {
        throw new UnsupportedOperationException("Please use a real generator!");
    }

}
