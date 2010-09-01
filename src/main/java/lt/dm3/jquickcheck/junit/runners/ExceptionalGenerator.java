package lt.dm3.jquickcheck.junit.runners;


public class ExceptionalGenerator implements Generator<Object> {

    @Override
    public Object generate() {
        throw new UnsupportedOperationException("Please use a real generator!");
    }

}
