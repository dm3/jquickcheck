package lt.dm3.jquickcheck.qc;

import java.util.List;

import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.QuickCheckResult;
import lt.dm3.jquickcheck.api.impl.DefaultQuickCheckResult;
import net.java.quickcheck.Generator;
import net.java.quickcheck.QuickCheck;
import net.java.quickcheck.characteristic.AbstractCharacteristic;

public class QCQuickCheckAdapter implements QuickCheckAdapter<Generator<?>> {

    private static class MultiGenerator implements Generator<Object[]> {

        private final List<Generator<?>> gens;
        private final int size;

        MultiGenerator(List<Generator<?>> gens) {
            this.gens = gens;
            this.size = gens.size();
        }

        public Object[] next() {
            Object[] values = new Object[size];
            for (int i = 0; i < size; i++) {
                values[i] = gens.get(i).next();
            }
            return values;
        }

    }

    public QuickCheckResult check(final PropertyInvocation<Generator<?>> invocation) {
        QuickCheckResult result = null;
        try {
            QuickCheck.forAll(invocation.settings().minSuccessful(),
                    new MultiGenerator(invocation.generators()),
                    new AbstractCharacteristic<Object[]>() {
                        @Override
                        protected void doSpecify(Object[] params) throws Throwable {
                            boolean result = invocation.invoke(params);
                            if (!result) {
                                throw new AssertionError("Falsified");
                            }
                        }
                    });
        } catch (Exception e) {
            result = DefaultQuickCheckResult.falsified(e);
        }
        if (result == null) {
            result = DefaultQuickCheckResult.proven();
        }
        return result;
    }

}
