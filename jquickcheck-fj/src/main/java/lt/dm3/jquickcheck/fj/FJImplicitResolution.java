package lt.dm3.jquickcheck.fj;

import static fj.Function.curry;

import java.lang.reflect.Method;
import java.util.List;

import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.impl.resolution.ResolutionOfImplicits;
import fj.F;
import fj.F2;
import fj.F3;
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
                            throw new QuickCheckException("Could not generate a value for generator: " + method);
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
                            throw new QuickCheckException("Could not generate a value for generator: " + method);
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
                            throw new QuickCheckException("Could not generate a value for generator: " + method);
                        }
                    }
                })));
	        default:
                throw new IllegalArgumentException("Unsupported number of generator components: " + components);
        }
    }

}
