package lt.dm3.jquickcheck.fj;

import static fj.Function.curry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.QuickCheckResult;
import lt.dm3.jquickcheck.api.impl.DefaultQuickCheckResult;
import fj.F;
import fj.F2;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.F6;
import fj.F7;
import fj.F8;
import fj.test.Arbitrary;
import fj.test.Arg;
import fj.test.CheckResult;
import fj.test.Property;
import fj.test.Result;

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

        @Override
        public Throwable exception() {
            return result.exception().toNull();
        }

        @Override
        public List<String> arguments() {
            return result.args().map(new F<fj.data.List<Arg<?>>, List<String>>() {
                @Override
                public List<String> f(fj.data.List<Arg<?>> a) {
                    List<String> result = new ArrayList<String>(a.length());
                    for (Arg<?> arg : a) {
                        result.add("(" + Arg.argShow.showS(arg) + ")");
                    }
                    return result;
                }
            }).orSome(Collections.<String> emptyList());
        }

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public QuickCheckResult check(final PropertyInvocation<Arbitrary<?>> invocation) {
        List<Arbitrary> generators = (List) invocation.generators();
        Settings settings = invocation.settings();
        switch (generators.size()) {
            case 0:
                return invokeOnce(invocation);
            case 1:
                return new FJQuickCheckResult(Property.property(generators.get(0), oneArg(invocation))
                                                                .check(settings.minSuccessful(), 500, 0, 100));
            case 2:
                return new FJQuickCheckResult(Property.property(generators.get(0), generators.get(1),
                                                                twoArgs(invocation))
                                                                .check(settings.minSuccessful(), 500, 0, 100));
            case 3:
                return new FJQuickCheckResult(Property.property(generators.get(0), generators.get(1),
                                                                generators.get(2), threeArgs(invocation))
                                                                .check(settings.minSuccessful(), 500, 0, 100));
            case 4:
                return new FJQuickCheckResult(Property.property(generators.get(0), generators.get(1),
                                                                generators.get(2), generators.get(3),
                                                                fourArgs(invocation)).check(settings.minSuccessful(),
                                                                                            500, 0, 100));
            case 5:
                return new FJQuickCheckResult(Property.property(generators.get(0), generators.get(1),
                                                                generators.get(2), generators.get(3),
                                                                generators.get(4), fiveArgs(invocation)).
                                                                check(settings.minSuccessful(), 500, 0, 100));
            case 6:
                return new FJQuickCheckResult(Property.property(generators.get(0), generators.get(1),
                                                                generators.get(2), generators.get(3),
                                                                generators.get(4), generators.get(5),
                                                                sixArgs(invocation)).check(settings.minSuccessful(),
                                                                                           500, 0, 100));
            case 7:
                return new FJQuickCheckResult(Property.property(generators.get(0), generators.get(1),
                                                                generators.get(2), generators.get(3),
                                                                generators.get(4), generators.get(5),
                                                                generators.get(6), sevenArgs(invocation)).
                                                                check(settings.minSuccessful(), 500, 0, 100));
            case 8:
                return new FJQuickCheckResult(Property.property(generators.get(0), generators.get(1),
                                                                generators.get(2), generators.get(3),
                                                                generators.get(4), generators.get(5),
                                                                generators.get(6), generators.get(7),
                                                                eightArgs(invocation)).check(settings.minSuccessful(),
                                                                                             500, 0, 100));
            default:
                throw new IllegalArgumentException("Unsupported number of generators: " + generators.size());
        }
    }

    private static Property dispatchResult(final PropertyInvocation<Arbitrary<?>> invocation, Object[] a) {
        switch (invocation.invoke(a)) {
            case DISCARDED:
                return Property.prop(Result.noResult());
            case PROVEN:
                return Property.prop(true);
            case FALSIFIED:
                return Property.prop(false);
            default:
                throw new IllegalStateException("Unsupported property result");
        }
    }

    private F<Object, Property> oneArg(final PropertyInvocation<Arbitrary<?>> invocation) {
        return new F<Object, Property>() {
            @Override
            public Property f(Object a) {
                return dispatchResult(invocation, new Object[] { a });
            }
        };
    }

    private F<Object, F<Object, Property>> twoArgs(final PropertyInvocation<Arbitrary<?>> invocation) {
        return curry(new F2<Object, Object, Property>() {
            @Override
            public Property f(Object a, Object b) {
                return dispatchResult(invocation, new Object[] { a, b });
            }
        });
    }

    private F<Object, F<Object, F<Object, Property>>> threeArgs(final PropertyInvocation<Arbitrary<?>> invocation) {
        return curry(new F3<Object, Object, Object, Property>() {
            @Override
            public Property f(Object a, Object b, Object c) {
                return dispatchResult(invocation, new Object[] { a, b, c });
            }
        });
    }

    private F<Object, F<Object, F<Object, F<Object, Property>>>> fourArgs(
        final PropertyInvocation<Arbitrary<?>> invocation) {
        return curry(new F4<Object, Object, Object, Object, Property>() {
            @Override
            public Property f(Object a, Object b, Object c, Object d) {
                return dispatchResult(invocation, new Object[] { a, b, c, d });
            }
        });
    }

    private F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>> fiveArgs(
        final PropertyInvocation<Arbitrary<?>> invocation) {
        return curry(new F5<Object, Object, Object, Object, Object, Property>() {
            @Override
            public Property f(Object a, Object b, Object c, Object d, Object e) {
                return dispatchResult(invocation, new Object[] { a, b, c, d, e });
            }
        });
    }

    private F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>> sixArgs(
        final PropertyInvocation<Arbitrary<?>> invocation) {
        return curry(new F6<Object, Object, Object, Object, Object, Object, Property>() {
            @Override
            public Property f(Object a, Object b, Object c, Object d, Object e, Object f) {
                return dispatchResult(invocation, new Object[] { a, b, c, d, e, f });
            }
        });
    }

    private F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>> sevenArgs(
        final PropertyInvocation<Arbitrary<?>> invocation) {
        return curry(new F7<Object, Object, Object, Object, Object, Object, Object, Property>() {
            @Override
            public Property f(Object a, Object b, Object c, Object d, Object e, Object f, Object g) {
                return dispatchResult(invocation, new Object[] { a, b, c, d, e, f, g });
            }
        });
    }

    private F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>>> eightArgs(
        final PropertyInvocation<Arbitrary<?>> invocation) {
        return curry(new F8<Object, Object, Object, Object, Object, Object, Object, Object, Property>() {
            @Override
            public Property f(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h) {
                return dispatchResult(invocation, new Object[] { a, b, c, d, e, f, g, h });
            }
        });
    }

    private QuickCheckResult invokeOnce(final PropertyInvocation<Arbitrary<?>> invocation) {
        try {
            switch (invocation.invoke()) {
                case PROVEN:
                    return DefaultQuickCheckResult.proven();
                case FALSIFIED:
                    return DefaultQuickCheckResult.falsified();
                case DISCARDED:
                    return DefaultQuickCheckResult.exhausted();
            }
        } catch (RuntimeException e) {
            return DefaultQuickCheckResult.falsified(e);
        }
        return DefaultQuickCheckResult.falsified();
    }
}
