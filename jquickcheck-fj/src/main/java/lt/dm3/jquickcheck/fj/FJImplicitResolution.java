package lt.dm3.jquickcheck.fj;

import static fj.Function.curry;

import java.lang.reflect.Method;
import java.util.List;

import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.impl.resolution.ResolutionOfImplicits;
import fj.F;
import fj.F2;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.F6;
import fj.F7;
import fj.F8;
import fj.test.Arbitrary;

public class FJImplicitResolution extends ResolutionOfImplicits<Arbitrary<?>> {

    public FJImplicitResolution() {
        super(Arbitrary.class);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Arbitrary<?> createImplicitGenerator(final Object context, final Method method,
        final List<Arbitrary<?>> components) {
        switch (components.size()) {
            case 1:
                return Arbitrary.arbitrary(components.get(0).gen.map(new F() {
                    @Override
                    public Object f(Object a) {
                        try {
                            return method.invoke(context, a);
                        } catch (Exception e) {
                            throw couldntGenerate(method, e);
                        }
                    }
                }));
            case 2:
                return Arbitrary.arbitrary(components.get(0).gen.bind(components.get(1).gen, curry(new F2() {
                    @Override
                    public Object f(Object a, Object b) {
                        try {
                            return method.invoke(context, a, b);
                        } catch (Exception e) {
                            throw couldntGenerate(method, e);
                        }
                    }
                })));
            case 3:
                return Arbitrary.arbitrary(components.get(0).gen.bind(components.get(1).gen, components.get(2).gen, 
                                                                      curry(new F3() {
                    @Override
                    public Object f(Object a, Object b, Object c) {
                        try {
                            return method.invoke(context, a, b, c);
                        } catch (Exception e) {
                            throw couldntGenerate(method, e);
                        }
                    }
                })));
            case 4:
                return Arbitrary.arbitrary(components.get(0).gen.bind(components.get(1).gen, components.get(2).gen, 
                                                                      components.get(3).gen, curry(new F4() {
                    @Override
                    public Object f(Object a, Object b, Object c, Object d) {
                        try {
                            return method.invoke(context, a, b, c, d);
                        } catch (Exception e) {
                            throw couldntGenerate(method, e);
                        }
                    }
                })));
            case 5:
                return Arbitrary.arbitrary(components.get(0).gen.bind(components.get(1).gen, components.get(2).gen, 
                                                                      components.get(3).gen, components.get(4).gen, curry(new F5() {
                    @Override
                    public Object f(Object a, Object b, Object c, Object d, Object e) {
                        try {
                            return method.invoke(context, a, b, c, d, e);
                        } catch (Exception x) {
                            throw couldntGenerate(method, x);
                        }
                    }
                })));
            case 6:
                return Arbitrary.arbitrary(components.get(0).gen.bind(components.get(1).gen, components.get(2).gen, 
                                                                      components.get(3).gen, components.get(4).gen, 
                                                                      components.get(5).gen, curry(new F6() {
                    @Override
                    public Object f(Object a, Object b, Object c, Object d, Object e, Object f) {
                        try {
                            return method.invoke(context, a, b, c, d, e, f);
                        } catch (Exception x) {
                            throw couldntGenerate(method, x);
                        }
                    }
                })));
            case 7:
                return Arbitrary.arbitrary(components.get(0).gen.bind(components.get(1).gen, components.get(2).gen, 
                                                                      components.get(3).gen, components.get(4).gen, 
                                                                      components.get(5).gen, components.get(6).gen, curry(new F7() {
                    @Override
                    public Object f(Object a, Object b, Object c, Object d, Object e, Object f, Object g) {
                        try {
                            return method.invoke(context, a, b, c, d, e, f, g);
                        } catch (Exception x) {
                            throw couldntGenerate(method, x);
                        }
                    }
                })));
            case 8:
                return Arbitrary.arbitrary(components.get(0).gen.bind(components.get(1).gen, components.get(2).gen, 
                                                                      components.get(3).gen, components.get(4).gen, 
                                                                      components.get(5).gen, components.get(6).gen, 
                                                                      components.get(7).gen, curry(new F8() {
                    @Override
                    public Object f(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h) {
                        try {
                            return method.invoke(context, a, b, c, d, e, f, g, h);
                        } catch (Exception x) {
                            throw couldntGenerate(method, x);
                        }
                    }
                })));
	        default:
                throw new IllegalArgumentException("Unsupported number of generator components: " + components);
        }
    }

    private QuickCheckException couldntGenerate(final Method method, Exception e) {
        return new QuickCheckException("Could not generate a value for generator: " + method, e);
    }

}
