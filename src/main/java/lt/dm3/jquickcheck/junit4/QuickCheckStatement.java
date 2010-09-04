package lt.dm3.jquickcheck.junit4;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

final class QuickCheckStatement extends Statement {

    private final QuickCheckExecution<?> execution;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    QuickCheckStatement(GeneratorRepository<?> generators, QuickCheckAdapter<?> adapter, FrameworkMethod method,
            Object target) {
        this.execution = new QuickCheckExecution(adapter, generators, method, target);
    }

    @Override
    public void evaluate() throws Throwable {
        execution.execute();
    }

}
