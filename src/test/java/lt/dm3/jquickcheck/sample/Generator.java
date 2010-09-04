package lt.dm3.jquickcheck.sample;

/**
 * Sample generator to be used in testing.
 * 
 * @author dm3
 * 
 * @param <T>
 *            type of generated element
 */
public interface Generator<T> {

    T generate();

}
