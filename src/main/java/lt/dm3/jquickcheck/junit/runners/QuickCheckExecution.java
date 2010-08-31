package lt.dm3.jquickcheck.junit.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.junit.runners.Generators.GeneratorsForTestCase;

import org.junit.runners.model.FrameworkMethod;

import fj.F;
import fj.test.Arbitrary;
import fj.test.CheckResult;
import fj.test.Gen;
import fj.test.Property;

public class QuickCheckExecution {

    private final GeneratorsForTestCase generators;
    private final FrameworkMethod method;
    private final Object target;

    public QuickCheckExecution(GeneratorsForTestCase generators, FrameworkMethod method, Object target) {
        this.generators = generators;
        this.method = method;
        this.target = target;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void execute() {
        Type[] args = method.getMethod().getGenericParameterTypes();
        Annotation[][] annotations = method.getMethod().getParameterAnnotations();
        if (args.length == 1) {
            final Type t = args[0];
            Arbitrary arb = null;
            if (annotations[0].length == 1) {
                Annotation ann = annotations[0][0];
                try {
                    Gen gen = Gen.gen(new FJGenAdapter(((Arb) ann).gen().newInstance()).adapt());
                    arb = Arbitrary.arbitrary(gen);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (generators.hasGeneratorFor(t)) {
                arb = Arbitrary.arbitrary(Gen.gen(new FJGenAdapter(generators.getGeneratorFor(t)).adapt()));
            }
            if (arb == null) {
                arb = ArgumentFactory.argumentFor(t);
            }
            CheckResult result = Property.property(arb, ShrinkFactory.shrinkFor(t), new F<Object, Property>() {
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
