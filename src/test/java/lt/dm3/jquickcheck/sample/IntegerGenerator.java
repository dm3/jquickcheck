package lt.dm3.jquickcheck.sample;

import java.util.Random;

public class IntegerGenerator implements Generator<Integer> {

    @Override
    public Integer generate() {
        return new Random().nextBoolean() ? 1 : -1;
    }

}
