package lt.dm3.jquickcheck.fj;

import lt.dm3.jquickcheck.Provider;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.api.PropertyMethodFactory;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.impl.DefaultPropertyMethodFactory;
import fj.test.Arbitrary;

public class FJ implements Provider<Arbitrary<?>> {

    @Override
    public QuickCheckAdapter<Arbitrary<?>> adapter() {
        return new FJQuickCheckAdapter();
    }

    @Override
    public GeneratorResolutionStrategy<Arbitrary<?>> resolutionStrategy() {
        return new FJGeneratorResolutionStrategy();
    }

    @Override
    public PropertyMethodFactory<Arbitrary<?>> methodFactory() {
        return new DefaultPropertyMethodFactory<Arbitrary<?>>();
    }
}
