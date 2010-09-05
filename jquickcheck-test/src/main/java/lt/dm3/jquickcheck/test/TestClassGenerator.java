package lt.dm3.jquickcheck.test;

import lt.dm3.jquickcheck.sample.Generator;

public class TestClassGenerator implements Generator<TestClass> {

    public TestClass generate() {
        return new TestClass();
    }
}
