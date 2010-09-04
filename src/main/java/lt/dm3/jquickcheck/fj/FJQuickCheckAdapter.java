package lt.dm3.jquickcheck.fj;

import lt.dm3.jquickcheck.api.Invocation;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.QuickCheckResult;
import lt.dm3.jquickcheck.junit4.Generator;
import fj.F;
import fj.test.Arbitrary;
import fj.test.CheckResult;
import fj.test.Gen;
import fj.test.Property;

public class FJQuickCheckAdapter implements QuickCheckAdapter {

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
    public QuickCheckResult check(Generator<?>[] generators, final Invocation invocation) {
        if (generators.length == 1) {
            Gen gen = Gen.gen(new FJGenAdapter(generators[0]).toFJ());
            return new FJQuickCheckResult(Property.property(Arbitrary.arbitrary(gen), new PropertyF(invocation))
                    .check());
        }
        throw new IllegalArgumentException("Unsupported number of generators: " + generators.length);
    }
}
