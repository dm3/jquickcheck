package lt.dm3.jquickcheck.fj;

import java.util.List;

import lt.dm3.jquickcheck.api.Invocation;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.QuickCheckResult;
import fj.F;
import fj.test.Arbitrary;
import fj.test.CheckResult;
import fj.test.Property;

public class FJQuickCheckAdapter implements QuickCheckAdapter<Arbitrary<?>> {

    private static final class FJQuickCheckResult implements QuickCheckResult {
        private final CheckResult result;

        FJQuickCheckResult(CheckResult result) {
            this.result = result;
        }

        @Override
        public boolean isPassed() {
            return result.isPassed();
        }

        @Override
        public boolean isProven() {
            return result.isProven();
        }

        @Override
        public boolean isFalsified() {
            return result.isFalsified();
        }

        @Override
        public boolean isExhausted() {
            return result.isExhausted();
        }
    }

    private static final class PropertyF extends F<Object, Property> {
        private final Invocation invocation;

        PropertyF(Invocation invocation) {
            this.invocation = invocation;
        }

        @Override
        public Property f(Object param) {
            try {
                return Property.prop(invocation.invoke(param));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public QuickCheckResult check(List<Arbitrary<?>> generators, final Invocation invocation) {
        if (generators.size() == 1) {
            return new FJQuickCheckResult(Property.property((Arbitrary) generators.get(0), 
                                          new PropertyF(invocation)).check());
        }
        throw new IllegalArgumentException("Unsupported number of generators: " + generators.size());
    }
}
