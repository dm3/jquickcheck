package lt.dm3.jquickcheck.sample;

import lt.dm3.jquickcheck.Provider;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.PropertyMethodFactory;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.impl.DefaultPropertyMethodFactory;

public class SampleProvider implements Provider<Generator<?>> {

    @Override
    public GeneratorResolutionStrategy<Generator<?>> resolutionStrategy() {
        return new SampleResolutionFromFields();
    }

    @Override
    public QuickCheckAdapter<Generator<?>> adapter() {
        return new SampleAdapter();
    }

    @Override
    public PropertyMethodFactory<Generator<?>> methodFactory(Settings settings) {
        return new DefaultPropertyMethodFactory<Generator<?>>(settings);
    }

}
