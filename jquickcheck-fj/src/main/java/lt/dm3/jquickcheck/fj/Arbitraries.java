package lt.dm3.jquickcheck.fj;

import fj.F;
import fj.data.List;
import fj.test.Arbitrary;

public class Arbitraries {

    /* This approach seems to be ~30% slower than the one without Arbitrary.arbArray
    public static final Arbitrary<int[]> arbIntArray = Arbitrary.arbitrary(Arbitrary.arbArray(Arbitrary.arbInteger).gen
            .map(new F<Array<Integer>, int[]>() {
                @Override
                public int[] f(Array<Integer> a) {
                    int[] ret = new int[a.length()];
                    for (int i = 0; i < a.length(); i++) {
                        ret[i] = a.get(i);
                    }
                    return ret;
                }
            }));*/

    public static final Arbitrary<int[]> arbIntArray = Arbitrary.arbitrary(Arbitrary.arbList(Arbitrary.arbInteger).gen
            .map(new F<List<Integer>, int[]>() {
                @Override
                public int[] f(List<Integer> a) {
                    int[] ret = new int[a.length()];
                    int cnt = 0;
                    for (Integer i : a) {
                        ret[cnt++] = i;
                    }
                    return ret;
                }
            }));

}
