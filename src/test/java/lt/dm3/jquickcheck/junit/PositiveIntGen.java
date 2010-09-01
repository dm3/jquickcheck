package lt.dm3.jquickcheck.junit;

import lt.dm3.jquickcheck.junit.runners.Generator;
import fj.test.Gen;
import fj.test.Rand;

public class PositiveIntGen implements Generator<Integer> {

    @Override
    public Integer generate() {
        return Gen.choose(1, Integer.MAX_VALUE).gen(10, Rand.standard);
    }

}
