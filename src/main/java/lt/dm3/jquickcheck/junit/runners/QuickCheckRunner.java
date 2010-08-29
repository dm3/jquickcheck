package lt.dm3.jquickcheck.junit.runners;

import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class QuickCheckRunner extends BlockJUnit4ClassRunner {

    public QuickCheckRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        return QuickCheckStatement.newStatement(method, test);
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
                errors.add(new Exception("Class " + eachTestMethod.getMethod().getDeclaringClass().getName() + " should be public"));
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
