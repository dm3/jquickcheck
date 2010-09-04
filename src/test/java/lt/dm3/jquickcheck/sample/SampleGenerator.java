package lt.dm3.jquickcheck.sample;

public class SampleGenerator implements Generator<Sample> {

    @Override
    public Sample generate() {
        return new Sample();
    }

}
