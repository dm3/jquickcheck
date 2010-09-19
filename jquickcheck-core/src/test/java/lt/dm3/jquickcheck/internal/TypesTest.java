package lt.dm3.jquickcheck.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.sample.Generator;

import org.hamcrest.Matcher;
import org.junit.Test;

import com.googlecode.gentyref.TypeToken;

public class TypesTest {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldReturnAllOfTheSuperInterfacesOfAType() {
        Type x = new TypeToken<ArrayList<List<Integer>>>() {}.getType();

        Iterable<Class> supers = (List) Types.allSuperTypesOf(x);

        assertThat(supers, (Matcher) hasItems(ArrayList.class, List.class, AbstractList.class, Iterable.class));
        assertThat(supers, (Matcher) hasItems(Serializable.class, Cloneable.class, Object.class));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldReturnAllOfTheParameterizedSuperInterfacesOfAType() {
        Type x = new TypeToken<ArrayList<List<Integer>>>() {}.getType();

        List<Class<?>> supers = Types.allParameterizedSuperTypesOf(x);

        assertThat(supers, (Matcher) hasItems(ArrayList.class, List.class, AbstractList.class, Iterable.class));
        assertThat(supers, (Matcher) not(hasItems(Serializable.class, Cloneable.class, Object.class)));
    }

    @Test
    public void hasTypeArguments_shouldReturnTrueIfParameterizedByAtLeastOneArgument() {
        Type x = new TypeToken<ArrayList<List<Integer>>>() {}.getType();

        boolean result = Types.hasTypeArguments(x);

        assertThat(result, is(true));
    }

    @Test
    public void hasTypeArguments_shouldReturnFalseIfNotParameterized() {
        Type x = Integer.class;

        boolean result = Types.hasTypeArguments(x);

        assertThat(result, is(false));
    }

    @Test
    public void genericsNesting_shouldReturnTheNestingOfAParameterizedType() {
        Type x = new TypeToken<List<List<List<Integer>>>>() {}.getType();
        assertThat(Types.genericsNesting(x), equalTo(4));

        x = int.class;
        assertThat(Types.genericsNesting(x), equalTo(1));

        x = Object[].class;
        assertThat(Types.genericsNesting(x), equalTo(1));

        x = Object[][].class;
        assertThat(Types.genericsNesting(x), equalTo(1));

        x = new TypeToken<Generator<Object[]>>() {}.getType();
        assertThat(Types.genericsNesting(x), equalTo(2));
    }
}
