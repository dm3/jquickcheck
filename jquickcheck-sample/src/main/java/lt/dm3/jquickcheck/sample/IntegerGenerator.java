package lt.dm3.jquickcheck.sample;

import java.util.Random;

public class IntegerGenerator implements Generator<Integer> {

    private final Random random = new Random();

    @Override
    public Integer generate() {
        return random.nextBoolean() ? 1 : -1;
    }

}
