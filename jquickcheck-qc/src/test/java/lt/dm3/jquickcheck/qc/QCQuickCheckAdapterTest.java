package lt.dm3.jquickcheck.qc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyInvocation.Result;
import lt.dm3.jquickcheck.api.impl.DefaultInvocationSettings;
import lt.dm3.jquickcheck.internal.Annotations;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.PrimitiveGenerators;

import org.junit.Before;
import org.junit.Test;

public class QCQuickCheckAdapterTest {

    private QCQuickCheckAdapter adapter;

    @Before
    public void before() {
        adapter = new QCQuickCheckAdapter();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldRunTheInvocationSpecifiedNumberOfTimes() {
        PropertyInvocation<Generator<?>> in = mock(PropertyInvocation.class);
        QuickCheck ann = Annotations.newInstance(QuickCheck.class);
        given(in.settings()).willReturn(new DefaultInvocationSettings(ann));
        given(in.generators()).willReturn((List) Arrays.asList(PrimitiveGenerators.integers()));
        given(in.invoke(anyVararg())).willReturn(Result.PROVEN);

        adapter.check(in);

        verify(in, times(ann.minSuccessful())).invoke(anyVararg());
    }
}
