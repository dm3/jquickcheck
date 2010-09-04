package lt.dm3.jquickcheck.junit4;

import lt.dm3.jquickcheck.GeneratorRepository;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

final class QuickCheckStatement extends Statement {

    private final QuickCheckExecution execution;

    QuickCheckStatement(GeneratorRepository<Generator<?>> generators, FrameworkMethod method, Object target) {
        this.execution = new QuickCheckExecution(generators, method, target);
    }

    @Override
    public void evaluate() throws Throwable {
        execution.execute();
    }

}
