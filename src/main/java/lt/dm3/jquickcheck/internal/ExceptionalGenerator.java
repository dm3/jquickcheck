package lt.dm3.jquickcheck.internal;

import lt.dm3.jquickcheck.junit4.Generator;


public class ExceptionalGenerator implements Generator<Object> {

    @Override
    public Object generate() {
        throw new UnsupportedOperationException("Please use a real generator!");
    }

}
