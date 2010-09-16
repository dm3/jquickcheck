package lt.dm3.jquickcheck.fj;

import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import fj.F;
import fj.test.CheckResult;
import fj.test.Property;

public class ArbitrariesTest {

    @Ignore
    @Test
    public void shouldReturnAnArrayOfPrimitiveInt() {
        long runs = 100;
        long avg = 0;
        for (int i = 0; i < runs; i++) {
            CheckResult result = Property.property(Arbitraries.arbIntArray, new F<int[], Property>() {
                @Override
                public Property f(int[] a) {
                    return Property.prop(a.length >= 0);
                }
            }).check(100, 0, 20, 40);
            System.out.println(result);
        }

        for (int i = 0; i < runs; i++) {
            long start = System.nanoTime();
            CheckResult result = Property.property(Arbitraries.arbIntArray, new F<int[], Property>() {
                @Override
                public Property f(int[] a) {
                    return Property.prop(a.length >= 0);
                }
            }).check(1000, 0, 20, 40);
            long end = System.nanoTime();
            avg += (end - start);
            System.out.println(result);
        }
        System.out.println("Total: " + TimeUnit.NANOSECONDS.toMillis(avg) + " millis.");
    }
}
