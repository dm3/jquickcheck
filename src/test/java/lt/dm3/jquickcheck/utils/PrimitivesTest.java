package lt.dm3.jquickcheck.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import lt.dm3.jquickcheck.internal.Primitives;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PrimitivesTest {
    
    private final Type a, b;

    public PrimitivesTest(Type a, Type b) {
        this.a = a;
        this.b = b;
    }

    @Test
    public void shouldRecognizeIntegerTypes() {
        assertThat(Primitives.equalIgnoreWrapping(a, b), is(true));
    }
    
    /**
     * The trouble with @Parametrized tests in JUnit is - all non-parametrized tests in the same class are run the same amount of times.
     */
    @Test
    public void shouldReturnTrueIfTypesAreNonPrimitiveButEqual() {
        assertThat(Primitives.equalIgnoreWrapping(this.getClass(), this.getClass()), is(true));
    }

    @Parameters
    public static Collection<Object[]> types() {
        return Arrays.asList(new Object[][]{
                {int.class, Integer.class},     {boolean.class, Boolean.class},
                {Integer.class, int.class},     {Boolean.class, boolean.class},
                {int.class, int.class},         {boolean.class, boolean.class},
                {Integer.class, Integer.class}, {Boolean.class, Boolean.class},

                {long.class, Long.class},   {double.class, Double.class},
                {Long.class, long.class},   {Double.class, double.class},
                {long.class, long.class},   {double.class, double.class},
                {Long.class, Long.class},   {Double.class, Double.class},

                {float.class, Float.class}, {short.class, Short.class},
                {Float.class, float.class}, {Short.class, short.class},
                {float.class, float.class}, {short.class, short.class},
                {Float.class, Float.class}, {Short.class, Short.class},

                {char.class, Character.class},
                {Character.class, char.class},
                {char.class, char.class},
                {Character.class, Character.class},
                
                {byte.class, Byte.class},
                {Byte.class, byte.class},
                {byte.class, byte.class},
                {Byte.class, Byte.class},
        });
    }
}