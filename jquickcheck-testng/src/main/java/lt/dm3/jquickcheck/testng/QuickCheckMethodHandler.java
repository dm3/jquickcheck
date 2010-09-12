package lt.dm3.jquickcheck.testng;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import lt.dm3.jquickcheck.Provider;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyMethod;
import lt.dm3.jquickcheck.api.PropertyMethodFactory;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.QuickCheckResult;
import lt.dm3.jquickcheck.api.impl.DefaultInvocationSettings;

// TODO: Duplication with QuickCheckRunner and QuickCheckStatement
public class QuickCheckMethodHandler<GEN> implements MethodHandler {

    private Provider<GEN> provider;
    private GeneratorResolutionStrategy<GEN> strategy;
    private QuickCheckAdapter<GEN> adapter;
    private PropertyMethodFactory<GEN> methodFactory;

    public QuickCheckMethodHandler(Class<?> clazz) {
        QuickCheck ann = clazz.getAnnotation(QuickCheck.class);
        if (ann == null) {
            throw new IllegalStateException("Generator resolution strategy isn't specified!");
        }
        try {
            this.provider = createProvider(ann);
            this.strategy = provider.resolutionStrategy();
            this.adapter = provider.adapter();
            this.methodFactory = provider.methodFactory(new DefaultInvocationSettings(ann));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Provider<GEN> createProvider(QuickCheck ann) throws InstantiationException, IllegalAccessException {
        return (Provider<GEN>) ann.provider().newInstance();
    }

    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        GeneratorRepository<GEN> repository = strategy.resolve(self);
        PropertyMethod<GEN> method = methodFactory.createMethod(proceed, self);
        PropertyInvocation<GEN> invocation = method.createInvocationWith(repository);
        QuickCheckResult result = adapter.check(invocation);
        if (!result.isPassed() && !result.isProven()) {
            throw new QuickCheckException(result);
        }
        return null;
    }

}
