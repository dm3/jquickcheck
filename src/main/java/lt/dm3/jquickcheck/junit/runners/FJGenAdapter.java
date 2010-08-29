package lt.dm3.jquickcheck.junit.runners;

import java.util.Random;

import fj.F;
import fj.test.Rand;

class FJGenAdapter<T> {

    private final Generator<T> gen;

    FJGenAdapter(Generator<T> gen) {
        this.gen = gen;
    }

    F<Integer, F<Rand, Object>> adapt() {
        return new F<Integer, F<Rand, Object>>() {
            @Override
            public F<Rand, Object> f(final Integer size) {
                return new F<Rand, Object>() {
                    @Override
                    public Object f(Rand _) {
                        return gen.generate(new Random(), size);
                    }
                };
            }
        };
    }
}
