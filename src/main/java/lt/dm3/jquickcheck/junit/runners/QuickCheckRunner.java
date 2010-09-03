package lt.dm3.jquickcheck.junit.runners;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.fj.FJGeneratorRepository;

import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class QuickCheckRunner extends BlockJUnit4ClassRunner {

    private Generators generators;

    public QuickCheckRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        Statement result = super.classBlock(notifier);
        generators = collectGenerators(getTestClass());
        return result;
    }

    private Generators collectGenerators(TestClass testClass) {
        Field[] fields = testClass.getJavaClass().getDeclaredFields();
        Generators gens = new Generators();
        for (Field field : fields) {
            if (field.getAnnotation(Arb.class) != null && Generator.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                gens.add(field);
            }
        }
        return gens;
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return getTestClass().getAnnotatedMethods(Property.class);
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        return QuickCheckStatement.newStatement(new FJGeneratorRepository(generators.forTest(test)), method, test);
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
