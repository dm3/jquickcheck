package lt.dm3.jquickcheck.fj;

import junit.framework.Assert;
import lt.dm3.jquickcheck.junit.runners.Generator;
import fj.F;
import fj.test.Gen;
import fj.test.Rand;

class FJGenAdapter<T> {

    private final Generator<T> generator;
    private final Gen<T> gen;
    private final int size;

    FJGenAdapter(Generator<T> gen) {
        this.generator = gen;
        this.gen = null;
        this.size = 0;
    }

    FJGenAdapter(Gen<T> gen, int size) {
        this.gen = gen;
        this.generator = null;
        this.size = size;
    }

    Generator<T> toGenerator() {
        return new Generator<T>() {
            @Override
            public T generate() {
                return gen.gen(size, Rand.standard);
            }
        };
    }

    F<Integer, F<Rand, Object>> toFJ() {
        Assert.assertNotNull(generator);

        return new F<Integer, F<Rand, Object>>() {
            @Override
            public F<Rand, Object> f(final Integer size) {
                return new F<Rand, Object>() {
                    @Override
                    public Object f(Rand _) {
                        return generator.generate();
                    }
                };
            }
        };
    }
}
