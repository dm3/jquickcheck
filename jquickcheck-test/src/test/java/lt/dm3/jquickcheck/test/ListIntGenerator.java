package lt.dm3.jquickcheck.test;

import java.util.Arrays;
import java.util.List;

import lt.dm3.jquickcheck.sample.Generator;

public class ListIntGenerator implements Generator<List<Integer>> {
    public List<Integer> generate() {
        return Arrays.asList(1, 2, 3);
    }
}