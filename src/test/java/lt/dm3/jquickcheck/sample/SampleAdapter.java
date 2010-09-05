package lt.dm3.jquickcheck.sample;

import java.util.List;

import lt.dm3.jquickcheck.api.PropertyInvocation;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.QuickCheckResult;
import lt.dm3.jquickcheck.api.impl.DefaultQuickCheckResult;

public class SampleAdapter implements QuickCheckAdapter<Generator<?>> {

    @Override
    public QuickCheckResult check(PropertyInvocation<Generator<?>> invocation) {
        Settings settings = invocation.settings();
        List<Generator<?>> generators = invocation.generators();
        boolean result = true;
        for (int i = 0; i < settings.minSuccessful(); i++) {
            Object[] params = new Object[generators.size()];
            for (int j = 0; j < generators.size(); j++) {
                params[j] = generators.get(j).generate();
            }
            result &= invocation.invoke(params);
        }
        return result ? DefaultQuickCheckResult.proven() : DefaultQuickCheckResult.falsified();
    }

}
