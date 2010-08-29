package lt.dm3.jquickcheck.junit.runners;

import java.lang.reflect.Type;

import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public final class QuickCheckStatement extends Statement {

    private final FrameworkMethod method;
    private final Object target;

    private QuickCheckStatement(FrameworkMethod method, Object target) {
        this.method = method;
        this.target = target;
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
        // TODO Auto-generated method stub

    }

}
