package lt.dm3.jquickcheck.sample;

public class PositiveIntGenerator implements Generator<Integer> {

    private final Generator<Integer> intGenerator = new IntegerGenerator();

    @Override
    public Integer generate() {
        Integer result = intGenerator.generate();
        return result < 0 ? -result : result == 0 ? 1 : result;
    }

}
