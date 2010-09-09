package lt.dm3.jquickcheck.junit4;

import java.lang.reflect.Modifier;
import java.util.List;

import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.Provider;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.api.PropertyMethodFactory;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.impl.DefaultInvocationSettings;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class QuickCheckRunner<GEN> extends BlockJUnit4ClassRunner {

    private Provider<GEN> provider;
    private GeneratorResolutionStrategy<GEN> strategy;
    private QuickCheckAdapter<GEN> adapter;
    private PropertyMethodFactory<GEN> methodFactory;

    public QuickCheckRunner(Class<?> klass) throws InitializationError {
        super(klass);
        initializeResolutionStrategy(klass);
    }

    private void initializeResolutionStrategy(Class<?> klass) {
        QuickCheck ann = klass.getAnnotation(QuickCheck.class);
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

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return getTestClass().getAnnotatedMethods(Property.class);
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        return new QuickCheckStatement<GEN>(strategy.resolve(test), adapter,
                                            methodFactory.createMethod(method.getMethod(), test));
    }

    /**
     * Remove the validation for no-args.
     * 
     * @see org.junit.runners.BlockJUnit4ClassRunner#validateTestMethods(java.util.List)
     */
    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(Test.class);

        for (FrameworkMethod eachTestMethod : methods) {
            if (!Modifier.isPublic(eachTestMethod.getMethod().getDeclaringClass().getModifiers())) {
                errors.add(new Exception("Class " + eachTestMethod.getMethod().getDeclaringClass().getName()
                        + " should be public"));
            }
            if (!Modifier.isPublic(eachTestMethod.getMethod().getModifiers())) {
                errors.add(new Exception("Method " + eachTestMethod.getMethod().getName() + "() should be public"));
            }
            if (eachTestMethod.getMethod().getReturnType() != Boolean.TYPE) {
                errors.add(new Exception("Method " + eachTestMethod.getMethod().getName() + "() should be boolean"));
            }
        }
    }

}
