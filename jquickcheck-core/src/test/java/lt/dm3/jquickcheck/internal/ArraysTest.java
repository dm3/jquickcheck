package lt.dm3.jquickcheck.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.sample.Generator;

import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

public class ArraysTest {

    @Test
    public void isArray_shouldReturnTrueOnGenericArrayType() {
        Type x = new TypeToken<Generator<Object>[]>() {}.getType();

        assertThat(Arrays.isArray(x), is(true));
    }

    @Test
    public void isArray_shouldReturnTrueOnNonGenericType() {
        Type x = int[].class;
        assertThat(Arrays.isArray(x), is(true));

        x = int[][].class;
        assertThat(Arrays.isArray(x), is(true));

        x = Object[].class;
        assertThat(Arrays.isArray(x), is(true));
    }

}