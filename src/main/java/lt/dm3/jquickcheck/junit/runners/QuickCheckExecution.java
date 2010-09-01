package lt.dm3.jquickcheck.junit.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import lt.dm3.jquickcheck.Invocation;
import lt.dm3.jquickcheck.QuickCheckAdapter;
import lt.dm3.jquickcheck.QuickCheckResult;
import lt.dm3.jquickcheck.fj.FJQuickCheckAdapter;
import lt.dm3.jquickcheck.junit.runners.Generators.GeneratorRepository;

import org.junit.runners.model.FrameworkMethod;

public class QuickCheckExecution {

    private final QuickCheckAdapter adapter = new FJQuickCheckAdapter();
    private final GeneratorRepository generators;
    private final FrameworkMethod method;
    private final Object target;

    public QuickCheckExecution(GeneratorRepository generators, FrameworkMethod method, Object target) {
        this.generators = generators;
        this.method = method;
        this.target = target;
    }

    public void execute() {
        Type[] args = method.getMethod().getGenericParameterTypes();
        Annotation[][] annotations = method.getMethod().getParameterAnnotations();
        if (args.length == 1) {
            final Type t = args[0];
            Generator<?> gen = null;
            if (annotations[0].length == 1) {
                Annotation ann = annotations[0][0];
                if (ann instanceof Arb) {
                    Arb arbAnnotation = (Arb) ann;
                    if (arbAnnotation.gen().isEmpty() || !generators.hasGeneratorFor(arbAnnotation.gen())) {
                        try {
                            gen = ((Arb) ann).genClass().newInstance();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        gen = generators.getGeneratorFor(arbAnnotation.gen());
                    }
                }
            } else if (generators.hasGeneratorFor(t)) {
                gen = generators.getGeneratorFor(t);
            }
            if (gen == null) {
                gen = generators.getDefaultGeneratorFor(t);
            }
            QuickCheckResult result = adapter.check(new Generator[] { gen }, new Invocation() {
                @Override
                public boolean invoke(Object param) {
                    try {
                        return (Boolean) method.invokeExplosively(target, param);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            if (!result.isPassed() && !result.isProven()) {
                throw new QuickCheckException(result);
            }
        }
    }
}
