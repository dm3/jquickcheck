package lt.dm3.jquickcheck.junit.runners;

import java.util.Random;

public interface Generator<T> {

    T generate(Random r, int size);
}
