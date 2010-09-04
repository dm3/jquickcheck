package lt.dm3.jquickcheck.junit4;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.GeneratorRepository;

import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public final class QuickCheckStatement extends Statement {

    private final QuickCheckExecution execution;

    private QuickCheckStatement(GeneratorRepository<Generator<?>> generators, FrameworkMethod method, Object target) {
        this.execution = new QuickCheckExecution(generators, method, target);
    }

    public static Statement newStatement(GeneratorRepository<Generator<?>> generators, FrameworkMethod method,
            Object test) {
        Type[] parameters = method.getMethod().getGenericParameterTypes();
        if (parameters.length == 0) {
            return new InvokeMethod(method, test);
        }
        return new QuickCheckStatement(generators, method, test);
    }

    @Override
    public void evaluate() throws Throwable {
        execution.execute();
    }

}
