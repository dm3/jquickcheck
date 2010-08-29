package lt.dm3.jquickcheck.junit;

import java.util.Random;

import lt.dm3.jquickcheck.junit.runners.Generator;
import fj.test.Gen;
import fj.test.Rand;

public class PositiveIntGen implements Generator<Integer> {

    @Override
    public Integer generate(Random r, int size) {
        return Gen.choose(1, Integer.MAX_VALUE).gen(size, Rand.standard);
    }

}
