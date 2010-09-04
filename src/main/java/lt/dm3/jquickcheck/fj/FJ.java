package lt.dm3.jquickcheck.fj;

import lt.dm3.jquickcheck.Provider;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import fj.test.Arbitrary;

public class FJ implements Provider<Arbitrary<?>> {

    public QuickCheckAdapter<Arbitrary<?>> adapter() {
        return new FJQuickCheckAdapter();
    }

    public GeneratorResolutionStrategy<Arbitrary<?>> resolutionStrategy() {
        return new FJGeneratorResolutionStrategy();
    }
}
