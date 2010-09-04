package lt.dm3.jquickcheck.fj;

import fj.F;
import fj.test.Arbitrary;

public abstract class Gens {
    private static final F<Integer, Integer> AS_POSITIVE = new F<Integer, Integer>() {
        @Override
        public Integer f(Integer a) {
            return a < 0 ? -a : a == 0 ? 1 : a;
        }
    };

    public static final Arbitrary<Integer> POSITIVE_INTEGERS = Arbitrary.arbitrary(
										            Arbitrary.arbInteger.gen.map(AS_POSITIVE));

    private Gens() {
        // static utils
    }

}
