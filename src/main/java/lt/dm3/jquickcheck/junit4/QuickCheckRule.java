package lt.dm3.jquickcheck.junit4;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class QuickCheckRule implements MethodRule {

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        try {
            method.invokeExplosively(target, 1);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return base;
    }

}
