package lt.dm3.jquickcheck.junit4;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.AbstractList;

import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.Invocation;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.QuickCheckResult;

import org.junit.runners.model.FrameworkMethod;

public class QuickCheckExecution<GEN> {

    final class Generators extends AbstractList<GEN> {
        private final Object[] generators;

        public Generators(GEN... gens) {
            this.generators = gens;
        }

        @SuppressWarnings("unchecked")
        @Override
        public GEN get(int index) {
            return (GEN) generators[index];
        }

        @Override
        public int size() {
            return generators.length;
        }

    }

    private final QuickCheckAdapter<GEN> adapter;
    private final GeneratorRepository<GEN> generators;
    private final FrameworkMethod method;
    private final Object target;

    public QuickCheckExecution(QuickCheckAdapter<GEN> adapter, GeneratorRepository<GEN> generators,
            FrameworkMethod method, Object target) {
        this.adapter = adapter;
        this.generators = generators;
        this.method = method;
        this.target = target;
    }

    public void execute() {
        Type[] args = method.getMethod().getGenericParameterTypes();
        Annotation[][] annotations = method.getMethod().getParameterAnnotations();
        if (args.length == 0) {
            boolean successful = false;
            try {
                successful = (Boolean) method.invokeExplosively(target, (Object[]) null);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            if (!successful) {
                throw QuickCheckException.falsified();
            }
        } else if (args.length == 1) {
            final Type t = args[0];
            GEN gen = null;
            if (annotations[0].length == 1) {
                Annotation ann = annotations[0][0];
                if (ann instanceof G) {
                    G arbAnnotation = (G) ann;
                    gen = generators.getGeneratorFor(arbAnnotation.gen());
                }
            } else if (generators.hasGeneratorFor(t)) {
                gen = generators.getGeneratorFor(t);
            }
            if (gen == null) {
                gen = generators.getDefaultGeneratorFor(t);
            }
            @SuppressWarnings("unchecked")
            QuickCheckResult result = adapter.check(new Generators(gen), new Invocation() {
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
