package lt.dm3.jquickcheck.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PrimitivesTest {

    private final Type a, b, primitive, wrapper;

    public PrimitivesTest(Type a, Type b, Type primitive, Type wrapper) {
        this.a = a;
        this.b = b;
        this.primitive = primitive;
        this.wrapper = wrapper;
    }

    @Test
    public void equalIgnoreWrapping_shouldRecognizeIntegerTypes() {
        assertThat(Primitives.equalIgnoreWrapping(a, b), is(true));
    }

    /**
     * The trouble with @Parametrized tests in JUnit is - all non-parametrized tests in the same class are run the same
     * amount of times.
     */
    @Test
    public void equalIgnoreWrapping_shouldReturnTrueIfTypesAreNonPrimitiveButEqual() {
        assertThat(Primitives.equalIgnoreWrapping(this.getClass(), this.getClass()), is(true));
    }

    @Test
    public void isPrimitiveOrWrapper_shouldReturnTrueIfArgumentIsPrimitiveOrHasPrimitiveOpposite() {
        assertThat(Primitives.isPrimitiveOrWrapper(primitive), is(true));
        assertThat(Primitives.isPrimitiveOrWrapper(wrapper), is(true));
        assertThat(Primitives.isPrimitiveOrWrapper(this.getClass()), is(false));
    }

    @Test
    public void opposite_shouldReturnTheOppositeOfTheGivenPremiumOrWrapper() {
        assertThat(Primitives.oppositeOf(primitive), equalTo(wrapper));
        assertThat(Primitives.oppositeOf(wrapper), equalTo(primitive));
    }

    @Test(expected = IllegalArgumentException.class)
    public void opposite_shouldFailIfGivenANonPrimitive() {
        Primitives.oppositeOf(this.getClass());
    }

    @Parameters
    public static Collection<Object[]> types() {
        return Arrays
                .asList(new Object[][] { { int.class, Integer.class, int.class, Integer.class },
                        { Integer.class, int.class, int.class, Integer.class },
                        { int.class, int.class, int.class, Integer.class },
                        { Integer.class, Integer.class, int.class, Integer.class },

                        { boolean.class, Boolean.class, boolean.class, Boolean.class },
                        { Boolean.class, boolean.class, boolean.class, Boolean.class },
                        { boolean.class, boolean.class, boolean.class, Boolean.class },
                        { Boolean.class, Boolean.class, boolean.class, Boolean.class },

                        { long.class, Long.class, long.class, Long.class },
                        { Long.class, long.class, long.class, Long.class },
                        { long.class, long.class, long.class, Long.class },
                        { Long.class, Long.class, long.class, Long.class },

                        { double.class, Double.class, double.class, Double.class },
                        { Double.class, double.class, double.class, Double.class },
                        { double.class, double.class, double.class, Double.class },
                        { Double.class, Double.class, double.class, Double.class },

                        { float.class, Float.class, float.class, Float.class },
                        { Float.class, float.class, float.class, Float.class },
                        { float.class, float.class, float.class, Float.class },
                        { Float.class, Float.class, float.class, Float.class },

                        { short.class, Short.class, short.class, Short.class },
                        { Short.class, short.class, short.class, Short.class },
                        { short.class, short.class, short.class, Short.class },
                        { Short.class, Short.class, short.class, Short.class },

                        { char.class, Character.class, char.class, Character.class },
                        { Character.class, char.class, char.class, Character.class },
                        { char.class, char.class, char.class, Character.class },
                        { Character.class, Character.class, char.class, Character.class },

                        { byte.class, Byte.class, byte.class, Byte.class },
                        { Byte.class, byte.class, byte.class, Byte.class },
                        { byte.class, byte.class, byte.class, Byte.class },
                        { Byte.class, Byte.class, byte.class, Byte.class }, });
    }
}