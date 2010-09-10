package lt.dm3.jquickcheck.fj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.QuickCheckResult;
import lt.dm3.jquickcheck.api.impl.DefaultQuickCheckResult;
import fj.F;
import fj.test.Arbitrary;
import fj.test.Arg;
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
                        result.add(Arg.argShow.showS(arg));
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

    private F<Object, Property> oneArg(final PropertyInvocation<Arbitrary<?>> invocation) {
        return new F<Object, Property>() {
            @Override
            public Property f(Object a) {
                return Property.prop(invocation.invoke(a));
            }
        };
    }

    private F<Object, F<Object, Property>> twoArgs(final PropertyInvocation<Arbitrary<?>> invocation) {
        return new F<Object, F<Object, Property>>() {
            @Override
            public F<Object, Property> f(final Object a) {
                return new F<Object, Property>() {
                    @Override
                    public Property f(Object b) {
                        return Property.prop(invocation.invoke(a, b));
                    }
                };
            }
        };
    }

    private F<Object, F<Object, F<Object, Property>>> threeArgs(final PropertyInvocation<Arbitrary<?>> invocation) {
        return new F<Object, F<Object, F<Object, Property>>>() {
            @Override
            public F<Object, F<Object, Property>> f(final Object a) {
                return new F<Object, F<Object, Property>>() {
                    @Override
                    public F<Object, Property> f(final Object b) {
                        return new F<Object, Property>() {
                            @Override
                            public Property f(final Object c) {
                                return Property.prop(invocation.invoke(a, b, c));
                            }
                        };
                    }
                };
            }
        };
    }

    private F<Object, F<Object, F<Object, F<Object, Property>>>> fourArgs(final PropertyInvocation<Arbitrary<?>> invocation) {
        return new F<Object, F<Object, F<Object, F<Object, Property>>>>() {
            @Override
            public F<Object, F<Object, F<Object, Property>>> f(final Object a) {
                return new F<Object, F<Object, F<Object, Property>>>() {
                    @Override
                    public F<Object, F<Object, Property>> f(final Object b) {
                        return new F<Object, F<Object, Property>>() {
                            @Override
                            public F<Object, Property> f(final Object c) {
                                return new F<Object, Property>() {
                                    @Override
                                    public Property f(final Object d) {
                                        return Property.prop(invocation.invoke(a, b, c, d));
                                    }
                                };
                            }
                        };
                    }
                };
            }
        };
    }

    private F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>> fiveArgs(final PropertyInvocation<Arbitrary<?>> invocation) {
        return new F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>() {
            @Override
            public F<Object, F<Object, F<Object, F<Object, Property>>>> f(final Object a) {
                return new F<Object, F<Object, F<Object, F<Object, Property>>>>() {
                    @Override
                    public F<Object, F<Object, F<Object, Property>>> f(final Object b) {
                        return new F<Object, F<Object, F<Object, Property>>>() {
                            @Override
                            public F<Object, F<Object, Property>> f(final Object c) {
                                return new F<Object, F<Object, Property>>() {
                                    @Override
                                    public F<Object, Property> f(final Object d) {
                                        return new F<Object, Property>() {
                                            @Override
                                            public Property f(final Object e) {
                                                return Property.prop(invocation.invoke(a, b, c, d, e));
                                            }
                                        };
                                    }
                                };
                            }
                        };
                    }
                };
            }
        };
    }

    private F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>> sixArgs(final PropertyInvocation<Arbitrary<?>> invocation) {
        return new F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>() {
            @Override
            public F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>> f(final Object a) {
                return new F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>() {
                    @Override
                    public F<Object, F<Object, F<Object, F<Object, Property>>>> f(final Object b) {
                        return new F<Object, F<Object, F<Object, F<Object, Property>>>>() {
                            @Override
                            public F<Object, F<Object, F<Object, Property>>> f(final Object c) {
                                return new F<Object, F<Object, F<Object, Property>>>() {
                                    @Override
                                    public F<Object, F<Object, Property>> f(final Object d) {
                                        return new F<Object, F<Object, Property>>() {
                                            @Override
                                            public F<Object, Property> f(final Object e) {
                                                return new F<Object, Property>() {
                                                    @Override
                                                    public Property f(final Object f) {
                                                        return Property.prop(invocation.invoke(a, b, c, d, e, f));
                                                    }
                                                };
                                            }
                                        };
                                    }
                                };
                            }
                        };
                    }
                };
            }
        };
    }

    private F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>> sevenArgs(final PropertyInvocation<Arbitrary<?>> invocation) {
        return new F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>>() {
            @Override
            public F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>> f(final Object a) {
                return new F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>() {
                    @Override
                    public F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>> f(final Object b) {
                        return new F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>() {
                            @Override
                            public F<Object, F<Object, F<Object, F<Object, Property>>>> f(final Object c) {
                                return new F<Object, F<Object, F<Object, F<Object, Property>>>>() {
                                    @Override
                                    public F<Object, F<Object, F<Object, Property>>> f(final Object d) {
                                        return new F<Object, F<Object, F<Object, Property>>>() {
                                            @Override
                                            public F<Object, F<Object, Property>> f(final Object e) {
                                                return new F<Object, F<Object, Property>>() {
                                                    @Override
                                                    public F<Object, Property> f(final Object f) {
                                                        return new F<Object, Property>() {
                                                            @Override
                                                            public Property f(final Object g) {
                                                                return Property.prop(invocation.invoke(a, b, c, d, e,
                                                                                                       f, g));
                                                            }
                                                        };
                                                    }
                                                };
                                            }
                                        };
                                    }
                                };
                            }
                        };
                    }
                };
            }
        };
    }

    private F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>>> eightArgs(final PropertyInvocation<Arbitrary<?>> invocation) {
        return new F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>>>() {
            @Override
            public F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>> f(final Object a) {
                return new F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>>() {
                    @Override
                    public F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>> f(final Object b) {
                        return new F<Object, F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>>() {
                            @Override
                            public F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>> f(final Object c) {
                                return new F<Object, F<Object, F<Object, F<Object, F<Object, Property>>>>>() {
                                    @Override
                                    public F<Object, F<Object, F<Object, F<Object, Property>>>> f(final Object d) {
                                        return new F<Object, F<Object, F<Object, F<Object, Property>>>>() {
                                            @Override
                                            public F<Object, F<Object, F<Object, Property>>> f(final Object e) {
                                                return new F<Object, F<Object, F<Object, Property>>>() {
                                                    @Override
                                                    public F<Object, F<Object, Property>> f(final Object f) {
                                                        return new F<Object, F<Object, Property>>() {
                                                            @Override
                                                            public F<Object, Property> f(final Object g) {
                                                                return new F<Object, Property>() {
                                                                    @Override
                                                                    public Property f(final Object h) {
                                                                        return Property.prop(invocation.invoke(a, b, c,
                                                                                                               d, e, f,
                                                                                                               g, h));
                                                                    };
                                                                };
                                                            }
                                                        };
                                                    }
                                                };
                                            }
                                        };
                                    }
                                };
                            }
                        };
                    }
                };
            }
        };
    }

    private QuickCheckResult invokeOnce(final PropertyInvocation<Arbitrary<?>> invocation) {
        try {
            if (invocation.invoke()) {
                return DefaultQuickCheckResult.proven();
            }
        } catch (RuntimeException e) {
            return DefaultQuickCheckResult.falsified(e);
        }
        return DefaultQuickCheckResult.falsified();
    }
}
