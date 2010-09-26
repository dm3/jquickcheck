package lt.dm3.jquickcheck.api.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class LookupByTypeThenDefaultTest {

    private Lookup<Type, Generator<?>> lookupNormal;
    private LookupDefaultByType<Generator<?>> lookupDefault;

    private LookupByTypeThenDefault<Generator<?>> lookup;

    @SuppressWarnings("unchecked")
    @Before
    public void before() {
        lookupNormal = mock(Lookup.class);
        lookupDefault = mock(LookupDefaultByType.class);

        lookup = new LookupByTypeThenDefault<Generator<?>>(lookupNormal, lookupDefault);
    }

    @Test
    public void has_shouldLookupUsingTheNormalLookupFirst() {
        Class<?> type = int.class;

        lookup.has(type);

        InOrder order = inOrder(lookupNormal, lookupDefault);
        order.verify(lookupNormal).has(type);
        order.verify(lookupDefault).hasDefault(type);
    }

    @Test
    public void hasOne_shouldLookupUsingTheNormalLookupFirst() {
        Class<?> type = int.class;

        lookup.hasOne(type);

        InOrder order = inOrder(lookupNormal, lookupDefault);
        order.verify(lookupNormal).hasOne(type);
        order.verify(lookupDefault).hasDefault(type);
    }

    @Test
    public void get_shouldGetFromTheNormalLookupBeforeQueryingTheDefault() {
        Class<?> type = int.class;
        given(lookupDefault.getDefault(type)).willThrow(new IllegalArgumentException());

        lookup.get(type);

        verify(lookupNormal).get(type);
    }

    @Test
    public void get_shouldGetFromTheDefaultLookupIfNormalDoesntExist() {
        Class<?> type = int.class;
        given(lookupNormal.get(type)).willThrow(new IllegalArgumentException());

        lookup.get(type);

        verify(lookupDefault).getDefault(type);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void getAll_shouldGetFromTheDefaultLookupIfNormalDoesntExist() {
        Class<?> type = int.class;
        IntegerGenerator gen = new IntegerGenerator();
        given(lookupNormal.getAll(type)).willReturn(new HashSet<Generator<?>>());
        given(lookupDefault.getDefault(type)).willReturn((Generator) gen);

        Set<Generator<?>> result = lookup.getAll(type);

        assertThat(result.size(), equalTo(1));
        assertThat(result.contains(gen), is(true));
    }
}
