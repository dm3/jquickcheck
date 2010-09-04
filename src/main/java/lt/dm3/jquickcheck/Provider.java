package lt.dm3.jquickcheck;

import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;

public interface Provider<GEN> {

    GeneratorResolutionStrategy<GEN> resolutionStrategy();

    QuickCheckAdapter<GEN> adapter();

}
