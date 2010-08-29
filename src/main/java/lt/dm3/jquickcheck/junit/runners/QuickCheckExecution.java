package lt.dm3.jquickcheck.junit.runners;

import java.lang.reflect.Type;

import org.junit.runners.model.FrameworkMethod;

import fj.F;
import fj.test.CheckResult;
import fj.test.Property;

public class QuickCheckExecution {

    private final FrameworkMethod method;
    private final Object target;

    public QuickCheckExecution(FrameworkMethod method, Object target) {
        this.method = method;
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    public void execute() {
        Type[] args = method.getMethod().getGenericParameterTypes();
        if (args.length == 1) {
            final Type t = args[0];
            CheckResult result = Property.property(ArgumentFactory.argumentFor(t), ShrinkFactory.shrinkFor(t), new F<Object, Property>() {
                @Override
                public Property f(Object param) {
                    try {
                        return Property.prop((Boolean) method.invokeExplosively(target, param));
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }).check();
            if (!result.isPassed() && !result.isProven()) {
                throw new QuickCheckException(result);
            }
        }
    }
}
