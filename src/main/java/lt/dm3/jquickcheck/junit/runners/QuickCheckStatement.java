package lt.dm3.jquickcheck.junit.runners;

import java.lang.reflect.Type;

import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public final class QuickCheckStatement extends Statement {

    private final QuickCheckExecution execution;

    private QuickCheckStatement(FrameworkMethod method, Object target) {
        this.execution = new QuickCheckExecution(method, target);
    }

    public static Statement newStatement(FrameworkMethod method, Object test) {
        Type[] parameters = method.getMethod().getGenericParameterTypes();
        if (parameters.length == 0) {
            return new InvokeMethod(method, test);
        }
        return new QuickCheckStatement(method, test);
    }

    @Override
    public void evaluate() throws Throwable {
        execution.execute();
    }

}
