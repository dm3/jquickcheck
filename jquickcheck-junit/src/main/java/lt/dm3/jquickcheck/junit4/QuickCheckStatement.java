package lt.dm3.jquickcheck.junit4;

import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyMethod;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.QuickCheckException;
import lt.dm3.jquickcheck.api.QuickCheckResult;

import org.junit.runners.model.Statement;

final class QuickCheckStatement<GEN> extends Statement {

    private final PropertyMethod<GEN> method;
    private final GeneratorRepository<GEN> repository;
    private final QuickCheckAdapter<GEN> adapter;

    QuickCheckStatement(GeneratorRepository<GEN> generators, QuickCheckAdapter<GEN> adapter, PropertyMethod<GEN> method) {
        this.method = method;
        this.adapter = adapter;
        this.repository = generators;
    }

    @Override
    public void evaluate() {
        PropertyInvocation<GEN> invocation = method.createInvocationWith(repository);
        QuickCheckResult result = adapter.check(invocation);
        if (!result.isPassed() && !result.isProven()) {
            throw new QuickCheckException(result);
        }
    }

}
