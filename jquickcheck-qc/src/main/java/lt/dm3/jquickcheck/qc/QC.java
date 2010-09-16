package lt.dm3.jquickcheck.qc;

import lt.dm3.jquickcheck.Provider;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.PropertyMethodFactory;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.impl.DefaultPropertyMethodFactory;
import net.java.quickcheck.Generator;

public class QC implements Provider<Generator<?>> {

    public GeneratorResolutionStrategy<Generator<?>> resolutionStrategy() {
        return new QCGeneratorResolutionStrategy();
    }

    public QuickCheckAdapter<Generator<?>> adapter() {
        return new QCQuickCheckAdapter();
    }

    public PropertyMethodFactory<Generator<?>> methodFactory(Settings settings) {
        return new DefaultPropertyMethodFactory<Generator<?>>(settings);
    }

}
